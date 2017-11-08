package com.minivision.machinefacerecognition.activity.utils;

import android.os.Environment;
import android.util.Log;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

/**
 * Created by Zhaojf on 2017/11/2 0002.
 */

public class DelFeature {

    File file;
    Document document;

    public DelFeature() throws DocumentException {
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minivision/FaceRecognize/Result/picLib.xml");
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(file);
    }

    public void deleteFeatureByName(String name) {


        try {

            Element rootElement = document.getRootElement();

            Log.d(TAG, "dom4j root = " + rootElement.getName());
            Iterator<Element> pIterator = rootElement.elementIterator();

            while (pIterator.hasNext()) {
                Element ePerson = pIterator.next();
                Log.d(TAG, "dom4j ePerson = " + ePerson.getName());

//            boolean remove = ePerson.getParent().remove(ePerson);


                Iterator<Element> epersonIterator = ePerson.elementIterator();

                while (epersonIterator.hasNext()) {
                    Element eName = epersonIterator.next();
                    Log.d(TAG, "dom4j eName  =  " + eName.getText());

                    if (eName.getText().equals(("\"" + (name) + "\""))) {
                        Log.d(TAG, "dom4j find sussess parent");
                        boolean remove = ePerson.getParent().remove(ePerson);
                        Log.d(TAG, "dom4j remove = " + remove);
                        break;
                    } else if (eName.getText().equals(name)) {
                        Log.d(TAG, "dom4j find sussess2 parent");
                        boolean remove = ePerson.getParent().remove(ePerson);
                        Log.d(TAG, "dom4j remove2 = " + remove);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveWriter() {
        try {
            // 回写操作
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());

            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
