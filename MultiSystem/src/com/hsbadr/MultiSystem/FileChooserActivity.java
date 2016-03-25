/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hsbadr.MultiSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.hsbadr.MultiSystem.fragments.CPUControl;
import com.hsbadr.MultiSystem.fragments.PluginFragment;
import com.hsbadr.MultiSystem.helpers.FileHelper;
import com.hsbadr.MultiSystem.pluggable.objects.Config;
import com.hsbadr.MultiSystem.pluggable.parsers.PluginParser;
import com.hsbadr.MultiSystem.R;
import com.hsbadr.MultiSystem.cards.CardButton;
import com.hsbadr.MultiSystem.cards.CardButtonDouble;
import com.hsbadr.MultiSystem.cards.CardSeekBarCombo;
import com.hsbadr.MultiSystem.helpers.CMDProcessor.CMDProcessor;
import com.hsbadr.MultiSystem.helpers.CMDProcessor.Shell;
import com.hsbadr.MultiSystem.helpers.Helpers;
import com.hsbadr.MultiSystem.helpers.SystemPropertiesReflection;
import com.hsbadr.MultiSystem.pluggable.objects.CardPlugin;
import com.hsbadr.MultiSystem.pluggable.parsers.ParserInterface;
import com.hsbadr.MultiSystem.pluggable.parsers.PluginParser;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardDownload;
import com.fima.cardsui.views.CardEditText;
import com.fima.cardsui.views.CardImage;
import com.fima.cardsui.views.CardPresentation;
import com.fima.cardsui.views.CardSeekBar;
import com.fima.cardsui.views.CardSpinner;
import com.fima.cardsui.views.CardSwitch;
import com.fima.cardsui.views.CardText;
import com.fima.cardsui.views.CardTextStripe;
import com.fima.cardsui.views.CardUI;

import com.ipaulpro.afilechooser.utils.FileUtils;

/**
 * @author paulburke (ipaulpro)
 */
public class FileChooserActivity extends Activity {

    private static final String TAG = "FileChooserActivity";

    private static final int REQUEST_CODE = 6384; // onActivityResult request
                                                  // code

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		showChooser();
		
    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, getString(R.string.chooser_title));
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
							String verify, putData;
							
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
							
                            Toast.makeText(FileChooserActivity.this,
                                    "File Selected: " + path, Toast.LENGTH_LONG).show();

							File file = new File(getFilesDir() + "/.MultiSystem/tmp/zipfile.new");
				 
							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}
				 
							FileWriter fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(path);
							bw.close();

//							FileReader fr = new FileReader(file.getAbsoluteFile());
//							BufferedReader br = new BufferedReader(fr);

//							while( (verify=br.readLine()) != null ){
//								if(verify != null){ //***edited
//									putData = verify.replaceAll("Select zip file to flash!", path);
//									bw.write(putData);
//								}
//							}
//							br.close();
									
                        } catch (Exception e) {
                            Log.e("FileSelectorTestActivity", "File select error", e);
                        }						
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

        finish();
		
		CMDProcessor.runSuCommand("MultiSystem.sh update");
	}
}
