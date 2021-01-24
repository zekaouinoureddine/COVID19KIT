package com.noured.covid19kit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GalleryPredictionActivity extends AppCompatActivity {
    
    protected Interpreter mtfLite;
    private int mImageSizeX;
    private int mImageSizeY;
    private TensorImage mInputImageBuffer;
    private TensorBuffer mOutputProbabilityBuffer;
    private TensorProcessor mProbabilityProcessor;
    public static final float IMAGE_MEAN = 0.0f;
    public static final float IMAGE_STD = 1.0f;
    public static final float PROBABILITY_MEAN = 0.0f;
    public static final float PROBABILITY_STD = 255.0f;

    private Bitmap mBitmap;
    Uri mImageUri;
    private List<String> mLabels;
    ImageView mImageView;
    Button mButtonPredict;
    TextView mPredictText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_prediction);

        mImageView = (ImageView) findViewById(R.id.prediction_activity_image);
        mButtonPredict = (Button) findViewById(R.id.prediction_activity_predict);
        mPredictText = (TextView) findViewById(R.id.prediction_activity_text);

        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select X-RAY Image"),12);
            }
        });

        try {
            mtfLite = new Interpreter(loadModelFile(this));
        }catch (Exception e){
            e.printStackTrace();
        }

        mButtonPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = mtfLite.getInputTensor(imageTensorIndex).shape();
                mImageSizeY = imageShape[1];
                mImageSizeX = imageShape[2];
                DataType imageDataType = mtfLite.getInputTensor(imageTensorIndex).dataType();


                int probabilityTensorIndex = 0;
                int[] probabilityShape = mtfLite.getOutputTensor(probabilityTensorIndex).shape();
                DataType probabilityDataType = mtfLite.getOutputTensor(probabilityTensorIndex).dataType();

                mInputImageBuffer = new TensorImage(imageDataType);
                mOutputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                mProbabilityProcessor = new TensorProcessor.Builder().add(getPostProcessNormalizeOp()).build();

                mInputImageBuffer = loadImage(mBitmap);

                mtfLite.run(mInputImageBuffer.getBuffer(), mOutputProbabilityBuffer.getBuffer().rewind());
                showResult();
            }
        });
    }

    private TensorImage loadImage(final Bitmap bitmap){
        // Load bitmap into a TensorImage.
        mInputImageBuffer.load(bitmap);

        // Create Processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(mImageSizeX, mImageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(getPreProcessNormalizeOp())
                .build();

        return imageProcessor.process(mInputImageBuffer);
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException{
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();

        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength);
    }

    private TensorOperator getPreProcessNormalizeOp(){
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private void showResult(){
        try {
            mLabels = FileUtil.loadLabels(this, "labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Float> labeledProbability = new TensorLabel(mLabels, mProbabilityProcessor.process(mOutputProbabilityBuffer))
                .getMapWithFloatValue();
        float maxValueMap = (Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()){
            if(entry.getValue() == maxValueMap){
                mPredictText.setText(entry.getKey());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode == RESULT_OK && data != null){
            mImageUri = data.getData();

            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImageView.setImageBitmap(mBitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}