package com.zamaruev.ds.dqn.maze.tensorflow;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Tensor;

import java.util.Random;

// In the fullness of time, equivalents of the methods of this class should be auto-generated from
// the OpDefs linked into libtensorflow_jni.so. That would match what is done in other languages
// like Python, C++ and Go.
public class GraphBuilder {

    private Graph g;
    private Random random = new Random();

    GraphBuilder(Graph g) {
        this.g = g;
    }

    Output div(Output x, Output y) {
        return binaryOp("Div", x, y);
    }

    Output sub(Output x, Output y) {
        return binaryOp("Sub", x, y);
    }

    Output add(String name, Output x, Output y) {
        return binaryOp(name, "Add", x, y);
    }

    Output matmul(Output x, Output y) {
        return binaryOp("MatMul", x, y);
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

    public Output constant(String name, Object value) {
        try (Tensor t = Tensor.create(value)) {
            return g.opBuilder("Const", name)
                    .setAttr("dtype", t.dataType())
                    .setAttr("value", t)
                    .build()
                    .output(0);
        }
    }

    Output placeholder(String name, Object value) {
        try (Tensor t = Tensor.create(value)) {
            return g.opBuilder("Placeholder", name)
                    .setAttr("dtype", t.dataType())
                    .build()
                    .output(0);
        }
    }

    Output variable(String name, Object value) {
        try (Tensor t = Tensor.create(value)) {
            return g.opBuilder("Variable", name)
                    .setAttr("dtype", t.dataType())
                    .build()
                    .output(0);
        }
    }

    Output truncatedNormal(Output shape) {
        return g.opBuilder("TruncatedNormal", "TruncatedNormal_" + random.nextInt(100))
                .addInput(shape)
                .setAttr("dtype", DataType.FLOAT)
                .build()
                .output(0);
    }

    Output reshape(Output input, Output shape) {
        return binaryOp("Reshape", input, shape);
    }

    Output relu(String name, Output hidden1) {
        return g.opBuilder("Relu", name).addInput(hidden1).build().output(0);
    }

    private Output binaryOp(String type, Output in1, Output in2) {
        return g.opBuilder(type, type + "_" + random.nextInt(100)).addInput(in1).addInput(in2).build().output(0);
    }

    private Output binaryOp(String name, String type, Output in1, Output in2) {
        return g.opBuilder(type, name).addInput(in1).addInput(in2).build().output(0);
    }

    public Output softmax(Output hidden2) {
        return g.opBuilder("Softmax", "Softmax").addInput(hidden2).build().output(0);
    }

    public Output square(final Output input) {
        return g.opBuilder("Square", "Square_" + random.nextInt(100)).addInput(input).build().output(0);
    }

    public Output reduce_mean(final Output input) {
        return g.opBuilder("Square", "ReduceMean_" + random.nextInt(100)).addInput(input).build().output(0);
    }

    public Output adam(final Output loss) {
        return g.opBuilder("GradientDescentOptimizer", "GradientDescentOptimizer_" + random.nextInt(100))
                .addInput(loss)
                .build()
                .output(0);

    }
}