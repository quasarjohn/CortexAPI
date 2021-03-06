package io.cortex.cortexapi.service;

import io.cortex.cortexapi.db_models.Classifier;
import io.cortex.cortexapi.db_models.User;
import io.cortex.cortexapi.models.classification_models.Classification;
import io.cortex.cortexapi.utils.SystemPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OnlineClassificationService {

    @Autowired
    UserService userService;

    @Autowired
    ClassifierService classifierService;

    private static final String PUBLIC_API_KEY = "publicapikey";

    public List<Classification> classifyImage(String api_key, String model_key, String img_url, int max_results, String order)
            throws UnsupportedEncodingException {

        String modelDir = SystemPaths.MODEL_DIR;
        /*
        If the api key used for the request is the public api key, allow access only to demo classifiers
         */
        if (api_key.equals(PUBLIC_API_KEY)) {
            switch (model_key) {
                case "bills":
                    modelDir = String.format(SystemPaths.CLASSIFIERS_DIR, "demo", "bills");
                    break;
                case "flowers":
                    modelDir = String.format(SystemPaths.CLASSIFIERS_DIR, "demo", "flowers");
                    break;
                case "hand_gestures":
                    modelDir = String.format(SystemPaths.CLASSIFIERS_DIR, "demo", "hand_gestures");
                    break;
                case "Dogs":
                    modelDir = String.format(SystemPaths.CLASSIFIERS_DIR, "demo", "Dogs");
                    break;
                default:
                    break;
            }
        }
        //check if the api key matches any users
        else {
            int count = 0;

            Iterable<User> users = userService.findUserByApiKey(api_key);

            for (User user : users) {
                System.out.println("Classification by " + user.getEmail());
                count++;
            }

            if (count == 0)
                return null;

            Classifier classifier = classifierService.findClassifierByKey(model_key);
            modelDir = String.format(SystemPaths.CLASSIFIERS_DIR,
                    classifier.getEmail(), classifier.getTitle());
        }

        List<Classification> classifications = new ArrayList<>();

        byte[] graphDef = readAllBytesOrExit(Paths.get(modelDir, "retrained_graph.pb"));
        List<String> labels =
                readAllLinesOrExit(Paths.get(modelDir, "retrained_labels.txt"));
        byte[] imageBytes = URL_to_byte(URLDecoder.decode(img_url, "UTF-8"));

        try (Tensor image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);
            int bestLabelIdx = maxIndex(labelProbabilities);
            System.out.println(
                    String.format(
                            "BEST MATCH: %s (%.2f%% likely)",
                            labels.get(bestLabelIdx), labelProbabilities[bestLabelIdx] * 100f));

            //this is to avoid out of bounds exception if the user sets a higher value than the size of the labels
            if (max_results > labels.size() || max_results == 0)
                max_results = labels.size();

            Classification[] classificationArray = new Classification[labels.size()];
            for (int i = 0; i < classificationArray.length; i++) {
                Classification c = new Classification();
                c.setLabel(labels.get(i));
                c.setProbability(labelProbabilities[i]);
                classificationArray[i] = c;
            }

            //sort the classifications
            if (order.equals("label_asc"))
                Arrays.sort(classificationArray, Classification.ClassificationLabelComparatorAsc);
            else if (order.equals("label_desc"))
                Arrays.sort(classificationArray, Classification.ClassificationLabelComparatorDesc);
            else if (order.equals("probability_asc"))
                Arrays.sort(classificationArray, Classification.ProbabilityComparatorAsc);
            else
                //if user enters anything, it will return the results in descending order based on the probability
                //this is also the default value
                Arrays.sort(classificationArray, Classification.ProbabilityComparatorDesc);

            for (int i = 0; i < max_results; i++) {
                classifications.add(classificationArray[i]);
            }
        }
        return classifications;
    }

    private byte[] URL_to_byte(String path) {
        byte[] imageInByte = null;
        try {
            BufferedImage img = ImageIO.read(new URL(path));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }

    private static Tensor constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
        try (Graph g = new Graph()) {
            GraphBuilder b = new GraphBuilder(g);
            // Some constants specific to the pre-trained model at:
            // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
            //
            // - The model was trained with images scaled to 224x224 pixels.
            // - The colors, represented as R, G, B in 1-byte each were converted to
            //   float using (value - Mean)/Scale.
            final int H = 299;
            final int W = 299;
            final float mean = 128;
            final float scale = 128f;

            // Since the graph is being constructed once per execution here, we can use a constant for the
            // input image. If the graph were to be re-used for multiple input images, a placeholder would
            // have been more appropriate.
            final Output input = b.constant("input", imageBytes);
            final Output output =
                    b.div(
                            b.sub(
                                    b.resizeBilinear(
                                            b.expandDims(
                                                    b.cast(b.decodeJpeg(input, 3), DataType.FLOAT),
                                                    b.constant("make_batch", 0)),
                                            b.constant("size", new int[]{H, W})),
                                    b.constant("mean", mean)),
                            b.constant("scale", scale));
            try (Session s = new Session(g)) {
                return s.runner().fetch(output.op().name()).run().get(0);
            }
        }
    }

    private static float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed("Mul", image).fetch("final_result").run().get(0)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }

    private static int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
        }
        return null;
    }

    private static List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    // In the fullness of time, equivalents of the methods of this class should be auto-generated from
    // the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
    // like Python, C++ and Go.
    static class GraphBuilder {
        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output div(Output x, Output y) {
            return binaryOp("Div", x, y);
        }

        Output sub(Output x, Output y) {
            return binaryOp("Sub", x, y);
        }

        Output resizeBilinear(Output images, Output size) {
            return binaryOp("ResizeBilinear", images, size);
        }

        Output expandDims(Output input, Output dim) {
            return binaryOp("ExpandDims", input, dim);
        }

        Output cast(Output value, DataType dtype) {
            return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
        }

        Output decodeJpeg(Output contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(contents)
                    .setAttr("channels", channels)
                    .build()
                    .output(0);
        }

        Output constant(String name, Object value) {
            try (Tensor t = Tensor.create(value)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }

        private Output binaryOp(String type, Output in1, Output in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
        }

        private Graph g;
    }
}