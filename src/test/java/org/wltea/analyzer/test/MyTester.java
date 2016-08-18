package org.wltea.analyzer.test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.wltea.analyzer.lucene.IKTokenizer;

/**
 * Created by qianjo on 8/16/2016.
 */

public class MyTester extends TestCase{

    String str = new String("马云和阿里巴巴都很牛。居然之家与欧特克之间有着战略合作。长春市长春药店。乒乓球拍卖啦。薄熙来到重庆。周杰轮周杰伦，范伟骑范玮琪。" +
            "Autodesk builds software that helps people imagine, design, and create a better world.");
    Reader input = new StringReader(str);

    public void testIKTokenizer(){
        testTokenizer(new IKTokenizer(input, false)); // 最细粒度切分
    }

    public void testIKTokenizerWithMaxWord(){
        testTokenizer(new IKTokenizer(input, true)); // 最大词长切分
    }

    private void testTokenizer(TokenStream tokenizer) {
        try {
            List<String> termList = new ArrayList<String>();

            while(tokenizer.incrementToken()){
                TermAttribute termAtt = tokenizer.getAttribute(TermAttribute.class);
                termList.add(termAtt.term());
            }

            System.out.println(String.join(" | ", termList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
