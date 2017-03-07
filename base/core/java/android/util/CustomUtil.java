/*
 * Copyright (C) 2009 The Advantech Open Source Project
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

package android.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

public class CustomUtil{
    
    private final static String TAG = "CustomUtil";
    private final static String CUST_PROPERTY_PATH = "/cust/info/cmds";
    
    public static String getValue(String key){
        String mPpropertys = new String();
        try {
            mPpropertys = FileRead(CUST_PROPERTY_PATH);
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
        
        String[] propertys = mPpropertys.split("[\r\n]+");
		for(String property:propertys){
			if(!property.contains("|"))
				continue;
			String[] value = property.split("\\|");
			if(value[1].equalsIgnoreCase(key)){
				return value[2];
			}
		}
		return null;
	}
    
    private static String FileRead(String filename) throws IOException{
        FileInputStream fis = new FileInputStream (new File(filename));
        byte[] b=new byte[fis.available()];
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();

        while((fis.read(b))!=-1){
            buffer.write(b);
        }
        byte[] data;
        data=buffer.toByteArray();
      
        buffer.close();
        fis.close();
        return new String(data);
    }
}
