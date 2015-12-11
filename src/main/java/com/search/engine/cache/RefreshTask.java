package com.search.engine.cache;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.search.engine.pojo.Doc;
import com.search.engine.pojo.DocInfo;
import com.search.engine.util.SegUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by yjj on 15/11/22.
 * 定时更新索引任务、虚拟化倒排文件，反虚拟化倒排文件任务
 */
@Service
public class RefreshTask {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTask.class);

    private static String TXT_FILE = RefreshTask.class.getResource("/").getPath().concat("search_data.txt");
    private static String SERIAL_FILE = RefreshTask.class.getResource("/").getPath().concat(String.valueOf(System.currentTimeMillis()).concat(".ivt"));

    private static long lastModified = 0;
    private static InvertCache invertCache = InvertCache.getInstance();

    @PostConstruct
    private void start() {

        logger.info("启动初始化倒排索引任务");
        new Thread(new Runnable() {


            public void run() {

                while (true) {

                    File file = new File(TXT_FILE);
                    long tmpVersion = file.lastModified();
                    if (tmpVersion > lastModified) {
                        buildIndex(TXT_FILE);
                        lastModified = tmpVersion;

                        //考虑内存还不理想，暂时不支持文件更新自动加载
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("线程休眠出现异常", e);
                    }
                }

            }
        }).start();
    }


    public void buildIndex(String fileName) {


        long begin = System.currentTimeMillis();
        logger.info("开始生成倒排");
        buildInvert(fileName);
        long end = System.currentTimeMillis();
        logger.info("生成倒排耗时{}毫秒", end - begin);

        begin = end;
        logger.info("开始计算rank值");
        invertCache.calculateRank();
        end = System.currentTimeMillis();
        logger.info("计算rerank值耗时{}毫秒", end - begin);

        begin = end;
        logger.info("开始序列化");
       // serialize(SERIAL_FILE);
        end = System.currentTimeMillis();
        logger.info("序列化完成{}", end - begin);

    }

    private void buildInvert(String fileName) {
        BufferedReader bufferedReader = null;
        try {

            Integer docId = 1;
            bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
            String tmpStr;
            while ((tmpStr = bufferedReader.readLine()) != null) {

                tmpStr = tmpStr.trim();
                if (tmpStr.length() == 0) {
                    continue;
                }

                Doc doc = new Doc(docId++, tmpStr);
                List<String> splitWorld = SegUtil.split(doc.getValue());
                Integer pos = 0;
                Multimap<String, Integer> worldPosition = ArrayListMultimap.create();
                for (String world : splitWorld) {
                    worldPosition.put(world.intern(), pos);
                    pos++;
                }

                //默认fieldId = 1
                DocInfo docInfo = new DocInfo(doc.getDocId(), 1, splitWorld.size(), worldPosition);
                invertCache.addDocInfo(docInfo);
            }

        } catch (Exception e) {
            logger.error("读取文件出现异常{}", e);
        } finally {

            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                logger.error("关闭bufferedReader时出现异常", e);
            }

        }
    }

    public void serialize(String fileName) {

    }

    public void deserialize(String fileName) {

    }

}
