package com.example.imageqolkasa;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener{
    private CustomArFragment arFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment= (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);

    }
    public void setupdatabase(Config config, Session session){
        Bitmap plenmap= BitmapFactory.decodeResource(getResources(),R.drawable.q);// for the image
        AugmentedImageDatabase aid=new AugmentedImageDatabase(session);
        aid.addImage("q",plenmap);//add the image to db
        config.setAugmentedImageDatabase(aid);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame=arFragment.getArSceneView().getArFrame();//collect all augmented images that traced
        Collection<AugmentedImage> images=frame.getUpdatedTrackables(AugmentedImage.class);//stor images in this
        for (AugmentedImage image: images){//for each image has beeb traced  check
            if(image.getTrackingState()== TrackingState.TRACKING){// if this image is traced
                if(image.getName().equals("q")){// and this image is similer to in one in db
                    Anchor anchor=image.createAnchor(image.getCenterPose()); //set an anchor to the center image
                    createModel(anchor);
                }

            }

        }
    }

    private void createModel(Anchor anchor) {//create model
        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placemodel(modelRenderable,anchor));//acept to set
    }

    private void placemodel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().onAddChild(anchorNode);// set the model scene

    }

}

