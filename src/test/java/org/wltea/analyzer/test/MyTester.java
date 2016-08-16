package org.wltea.analyzer.test;

import junit.framework.TestCase;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

/**
 * Created by qianjo on 8/16/2016.
 */
public class MyTester extends TestCase{

    public void testCustomizedDictionary(){
        String str = new String("马云和阿里巴巴都很牛。居然之家与欧特克之间有着战略合作。");
        IKAnalysis(str);
    }

    public String IKAnalysis(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            byte[] bt = str.getBytes();
            InputStream ip = new ByteArrayInputStream(bt);
            Reader read = new InputStreamReader(ip);
            IKSegmentation iks = new IKSegmentation(read, false); // true 用智能分词，false细粒度
            Lexeme t;
            while ((t = iks.next()) != null) {
                sb.append(t.getLexemeText() + " , ");
            }
            sb.delete(sb.length() - 3, sb.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
}
