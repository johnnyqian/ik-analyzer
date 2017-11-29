package org.johnnyqian.test;

import junit.framework.TestCase;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.wltea.analyzer.lucene.IKTokenizer;
import scala.Tuple2;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SparkWordCount extends TestCase {
    public void test(){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
        conf.set("spark.hadoop.validateOutputSpecs", "false");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //JavaRDD<String> input = sc.textFile("src/test/resources/射雕英雄传.txt");

        JavaRDD<String> input = sc.hadoopFile("src/test/resources/射雕英雄传.txt", TextInputFormat.class, LongWritable.class, Text.class).map(
                (Function<Tuple2<LongWritable, Text>, String>) pair -> new String(pair._2.getBytes(), 0, pair._2.getLength(), "gb2312")
        );

        JavaRDD<String> words = input.flatMap(
                (FlatMapFunction<String, String>) s -> Tokenization(new IKTokenizer(new StringReader(s), true))
        );

        JavaPairRDD<String, Integer> counts = words.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1)
        ).reduceByKey(
                (Function2<Integer, Integer, Integer>) (x, y) -> x + y
        );

        counts.filter(item -> item._1.length() > 1)
                .mapToPair(item -> item.swap())
                .sortByKey(false)
                .mapToPair(item -> item.swap())
                .map((Function<Tuple2<String, Integer>, String>) x-> x._1 + ',' + x._2)
                .saveAsTextFile("output");
    }

    private static List<String> Tokenization(TokenStream tokenizer) {
        List<String> termList = new ArrayList<>();
        try {
            while(tokenizer.incrementToken()){
                TermAttribute termAtt = tokenizer.getAttribute(TermAttribute.class);
                termList.add(termAtt.term());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return termList;
    }
}
