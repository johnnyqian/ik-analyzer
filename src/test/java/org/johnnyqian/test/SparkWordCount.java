package org.johnnyqian.test;

import junit.framework.TestCase;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

public class SparkWordCount extends TestCase {
    public void test(){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //JavaRDD<String> input = sc.textFile("src/test/resources/射雕英雄传.txt");

        JavaRDD<String> input = sc.hadoopFile("src/test/resources/射雕英雄传.txt", TextInputFormat.class, LongWritable.class, Text.class).map(
                (Function<Tuple2<LongWritable, Text>, String>) pair -> new String(pair._2.getBytes(), 0, pair._2.getLength(), "gb2312")
        );

        JavaRDD<String> words = input.flatMap(
                (FlatMapFunction<String, String>) s -> Arrays.asList(s.split(" "))
        );

        JavaPairRDD<String, Integer> counts = words.mapToPair(
                (PairFunction<String, String, Integer>) s -> new Tuple2<>(s, 1)
        ).reduceByKey(
                (Function2<Integer, Integer, Integer>) (x, y) -> x + y
        );

        counts.map(
                (Function<Tuple2<String, Integer>, String>) x-> x._1 + ',' + x._2
        ).saveAsTextFile("output");
    }
}
