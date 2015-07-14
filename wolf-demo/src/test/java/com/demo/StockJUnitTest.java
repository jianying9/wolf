package com.demo;

import com.wolf.framework.utils.StringUtils;
import com.wolf.framework.worker.context.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 *
 * @author jianying9
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StockJUnitTest extends AbstractDemoTest {

    public StockJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private static String sid;

    //
//    @Test
    public void test0101stockInsert() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("id", "600849");
        Response response = testHandler.execute("/stock/insert", parameterMap);
        System.out.println(response.getResponseMessage());
    }

//    @Test
    public void test0201stockMoneyFlowUpdate() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("id", "600649");
        Response response = testHandler.execute("/stock/moneyflow/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }

    @Test
    public void test0301UpdateStockMoneyFlowMinute() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", "minute");
        Response response = testHandler.execute("/stock/moneyflow/timer/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }

//    @Test
    public void test0401TruncateStockMoneyFlowMinute() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        Response response = testHandler.execute("/stock/moneyflow/minute/timer/truncate", parameterMap);
        System.out.println(response.getResponseMessage());
    }

//    @Test
    public void test0501UpdateStockMoneyFlowDay() {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", "day");
        Response response = testHandler.execute("/stock/moneyflow/timer/update", parameterMap);
        System.out.println(response.getResponseMessage());
    }

//    @Test
    public void test() {
        String text = "000001 深发展A\n"
                + "　　000002 万科A\n"
                + "　　000009 深宝安A\n"
                + "　　000012 南玻A\n"
                + "　　000021 深科技A\n"
                + "　　000022 深赤湾A\n"
                + "　　000024 招商地产\n"
                + "　　000027 深能源A\n"
                + "　　000029 G深深房\n"
                + "　　000031 深宝恒A\n"
                + "　　000036 华联控股\n"
                + "　　000039 中集集团\n"
                + "　　000059 辽通化工\n"
                + "　　000060 中金岭南\n"
                + "　　000061 农产品\n"
                + "　　000063 中兴通讯\n"
                + "　　000066 长城电脑\n"
                + "　　000068 赛格三星\n"
                + "　　000069 华侨城A\n"
                + "　　000088 盐田港A\n"
                + "　　000089 深圳机场\n"
                + "　　000099 中信海直\n"
                + "　　000100 TCL集团\n"
                + "　　000157 中联重科\n"
                + "　　000400 许继电气\n"
                + "　　000401 冀东水泥\n"
                + "　　000402 金融街\n"
                + "　　000410 沈阳机床\n"
                + "　　000422 湖北宜化\n"
                + "　　000423 东阿阿胶\n"
                + "　　000425 徐工科技\n"
                + "　　000488 晨鸣纸业\n"
                + "　　000503 海虹控股\n"
                + "　　000518 四环生物\n"
                + "　　000520 中国凤凰\n"
                + "　　000527 美的电器\n"
                + "　　000528 桂柳工A\n"
                + "　　000538 云南白药\n"
                + "　　000539 粤电力A\n"
                + "　　000541 佛山照明\n"
                + "　　000550 江铃汽车\n"
                + "　　000559 万向钱潮\n"
                + "　　000562 宏源证券 \n"
                + "　　000568 泸州老窖\n"
                + "　　000581 威孚高科\n"
                + "　　000599 G 双 星\n"
                + "　　000601 韶能股份\n"
                + "　　000607 华立控股\n"
                + "　　000617 石油济柴 \n"
                + "　　000623 吉林敖东\n"
                + "　　000625 长安汽车\n"
                + "　　000629 新钢钒\n"
                + "　　000630 铜陵有色　\n"
                + "　　000636 风华高科\n"
                + "　　000651 格力电器\n"
                + "　　000652 G 泰 达\n"
                + "　　000659 珠海中富\n"
                + "　　000680 山推股份\n"
                + "　　000682 东方电子\n"
                + "　　000698 沈阳化工\n"
                + "　　000707 双环科技\n"
                + "　　000708 大冶特钢\n"
                + "　　000709 唐钢股份\n"
                + "　　000717 韶钢松山\n"
                + "　　000725 京东方A\n"
                + "　　000726 鲁泰A\n"
                + "　　000729 燕京啤酒\n"
                + "　　000733 振华科技\n"
                + "　　000758 中色股份\n"
                + "　　000761 G 本 钢 \n"
                + "　　000767 漳泽电力\n"
                + "　　000768 西飞国际\n"
                + "　　000778 新兴铸管\n"
                + "　　000780 草原兴发\n"
                + "　　000786 北新建材\n"
                + "　　000792 盐湖钾肥\n"
                + "　　000793 G燃气 \n"
                + "　　000800 一汽轿车\n"
                + "　　000806 银河科技\n"
                + "　　000807 云铝股份\n"
                + "　　000822 山东海化\n"
                + "　　000825 太钢不锈\n"
                + "　　000828 东莞控股\n"
                + "　　000839 中信国安\n"
                + "　　000858 五粮液\n"
                + "　　000869 G张裕\n"
                + "　　000875 吉电股份\n"
                + "　　000878 云南铜业\n"
                + "　　000895 双汇发展\n"
                + "　　000898 鞍钢新轧\n"
                + "　　000900 现代投资\n"
                + "　　000912 泸天化\n"
                + "　　000917 电广传媒\n"
                + "　　000920 南方汇通\n"
                + "　　000927 一汽夏利\n"
                + "　　000930 丰原生化\n"
                + "　　000932 华菱管线\n"
                + "　　000933 神火股份\n"
                + "　　000937 金牛能源\n"
                + "　　000939 凯迪电力\n"
                + "　　000959 首钢股份\n"
                + "　　000960 锡业股份\n"
                + "　　000962 东方钽业\n"
                + "　　000968 煤气化\n"
                + "　　000969 G 安泰Ａ\n"
                + "　　000970 中科三环\n"
                + "　　000983 西山煤电\n"
                + "　　000997 G 新大陆 \n"
                + "　　002024 苏宁电器\n"
                + "　　600000 浦发银行\n"
                + "　　600001 邯郸钢铁\n"
                + "　　600004 白云机场\n"
                + "　　600005 武钢股份\n"
                + "　　600006 东风汽车\n"
                + "　　600008 首创股份\n"
                + "　　600009 上海机场\n"
                + "　　600010 钢联股份\n"
                + "　　600011 华能国际\n"
                + "　　600012 皖通高速\n"
                + "　　600015 华夏银行\n"
                + "　　600016 民生银行\n"
                + "　　600018 上港集箱\n"
                + "　　600019 宝钢股份\n"
                + "　　600020 中原高速\n"
                + "　　600021 上海电力\n"
                + "　　600022 济南钢铁\n"
                + "　　600026 中海发展\n"
                + "　　600027 华电国际\n"
                + "　　600028 中国石化\n"
                + "　　600029 南方航空\n"
                + "　　600030 中信证券\n"
                + "　　600031 三一重工\n"
                + "　　600033 福建高速\n"
                + "　　600035 楚天高速\n"
                + "　　600036 招商银行\n"
                + "　　600037 歌华有线\n"
                + "　　600050 中国联通\n"
                + "　　600057 夏新电子\n"
                + "　　600058 五矿发展\n"
                + "　　600060 海信电器\n"
                + "　　600062 双鹤药业\n"
                + "　　600073 上海梅林\n"
                + "　　600078 澄星股份\n"
                + "　　600085 同仁堂\n"
                + "　　600087 南京水运\n"
                + "　　600088 中视传媒\n"
                + "　　600089 特变电工\n"
                + "　　600091 明天科技\n"
                + "　　600096 云天化\n"
                + "　　600098 广州控股\n"
                + "　　600100 清华同方\n"
                + "　　600102 莱钢股份\n"
                + "　　600104 上海汽车\n"
                + "　　600108 亚盛集团\n"
                + "　　600110 中科英华\n"
                + "　　600115 东方航空\n"
                + "　　600117 西宁特钢\n"
                + "　　600121 郑州煤电\n"
                + "　　600123 兰花科创\n"
                + "　　600125 铁龙物流\n"
                + "　　600126 杭钢股份\n"
                + "　　600132 重庆啤酒 \n"
                + "　　600138 中青旅\n"
                + "　　600143 G金发 \n"
                + "　　600151 G航天 \n"
                + "　　600153 建发股份\n"
                + "　　600166 福田汽车\n"
                + "　　600170 上海建工\n"
                + "　　600171 上海贝岭\n"
                + "　　600177 雅戈尔\n"
                + "　　600183 生益科技\n"
                + "　　600188 兖州煤业\n"
                + "　　600196 复星医药\n"
                + "　　600198 大唐电信\n"
                + "　　600205 山东铝业\n"
                + "　　600207 安彩高科\n"
                + "　　600210 紫江企业\n"
                + "　　600215 长春经开\n"
                + "　　600220 江苏阳光\n"
                + "　　600221 海南航空\n"
                + "　　600231 凌钢股份\n"
                + "　　600236 桂冠电力\n"
                + "　　600256 广汇股份\n"
                + "　　600266 北京城建\n"
                + "　　600267 海正药业\n"
                + "　　600269 赣粤高速\n"
                + "　　600270 外运发展\n"
                + "　　600271 航天信息\n"
                + "　　600276 恒瑞医药 \n"
                + "　　600282 南钢股份\n"
                + "　　600296 兰州铝业\n"
                + "　　600299 星新材料 \n"
                + "　　600307 酒钢宏兴\n"
                + "　　600308 华泰股份\n"
                + "　　600309 烟台万华\n"
                + "　　600320 振华港机\n"
                + "　　600331 宏达股份\n"
                + "　　600333 长春燃气\n"
                + "　　600339 天利高新\n"
                + "　　600348 国阳新能\n"
                + "　　600350 山东基建\n"
                + "　　600357 承德钒钛\n"
                + "　　600361 G综超 \n"
                + "　　600362 江西铜业\n"
                + "　　600377 宁沪高速\n"
                + "　　600383 金地集团\n"
                + "　　600399 抚顺特钢\n"
                + "　　600406 国电南瑞 \n"
                + "　　600408 安泰集团\n"
                + "　　600410 G华胜 \n"
                + "　　600415 小商品城 \n"
                + "　　600418 G江汽\n"
                + "　　600428 中远航运\n"
                + "　　600456 G 宝 钛 \n"
                + "　　600460 G 士兰微 \n"
                + "　　600498 G 烽 火 \n"
                + "　　600500 中化国际\n"
                + "　　600508 上海能源\n"
                + "　　600519 贵州茅台\n"
                + "　　600521 G 华 海 \n"
                + "　　600535 G 天士力 \n"
                + "　　600548 深高速\n"
                + "　　600549 G 厦 钨 \n"
                + "　　600550 G 天 威\n"
                + "　　600569 安阳钢铁\n"
                + "　　600578 G 京 能 \n"
                + "　　600581 八一钢铁\n"
                + "　　600583 海油工程\n"
                + "　　600585 海螺水泥\n"
                + "　　600588 G 用 友 \n"
                + "　　600591 上海航空\n"
                + "　　600597 光明乳业\n"
                + "　　600598 北大荒\n"
                + "　　600600 青岛啤酒\n"
                + "　　600601 方正科技\n"
                + "　　600602 广电电子\n"
                + "　　600616 G 食 品 \n"
                + "　　600621 上海金陵\n"
                + "　　600627 上电股份\n"
                + "　　600628 G 新世界 \n"
                + "　　600631 百联股份\n"
                + "　　600635 大众公用\n"
                + "　　600637 广电信息\n"
                + "　　600639 浦东金桥\n"
                + "　　600642 申能股份\n"
                + "　　600643 爱建股份\n"
                + "　　600649 原水股份\n"
                + "　　600652 爱使股份\n"
                + "　　600653 申华控股\n"
                + "　　600654 飞乐股份\n"
                + "　　600655 G 豫园 \n"
                + "　　600660 福耀玻璃\n"
                + "　　600662 强生控股\n"
                + "　　600663 陆家嘴\n"
                + "　　600674 川投能源\n"
                + "　　600675 中华企业\n"
                + "　　600688 上海石化\n"
                + "　　600690 青岛海尔\n"
                + "　　600694 大商股份\n"
                + "　　600717 天津港\n"
                + "　　600718 东软股份\n"
                + "　　600724 宁波富达\n"
                + "　　600739 辽宁成大\n"
                + "　　600740 山西焦化\n"
                + "　　600741 巴士股份\n"
                + "　　600747 大显股份\n"
                + "　　600754 G 锦 江 \n"
                + "　　600770 综艺股份\n"
                + "　　600779 全兴股份\n"
                + "　　600780 通宝能源\n"
                + "　　600786 东方锅炉\n"
                + "　　600795 国电电力\n"
                + "　　600797 浙大网新\n"
                + "　　600808 马钢股份\n"
                + "　　600809 G 汾 酒 \n"
                + "　　600811 东方集团\n"
                + "　　600812 华北制药\n"
                + "　　600820 隧道股份\n"
                + "　　600832 东方明珠\n"
                + "　　600834 G 申地铁 \n"
                + "　　600835 上海机电\n"
                + "　　600839 四川长虹\n"
                + "　　600849 上海医药\n"
                + "　　600851 海欣股份\n"
                + "　　600854 春兰股份\n"
                + "　　600863 内蒙华电\n"
                + "　　600868 梅雁股份\n"
                + "　　600871 仪征化纤\n"
                + "　　600874 创业环保\n"
                + "　　600875 G 东 电 \n"
                + "　　600879 火箭股份\n"
                + "　　600881 亚泰集团\n"
                + "　　600884 杉杉股份\n"
                + "　　600886 国投电力\n"
                + "　　600887 伊利股份\n"
                + "　　600894 广钢股份\n"
                + "　　600895 张江高科\n"
                + "　　600900 长江电力\n"
                + "　　600970 中材国际\n"
                + "　　600997 开滦股份";
        String[] sArr = text.split("\n");
        System.out.println(sArr[0]);
        Map<String, String> parameterMap = new HashMap<String, String>();
        for (String s : sArr) {
            String[] infoArr = s.split(" ");
            String id = StringUtils.trim(infoArr[0]);
            parameterMap.put("id", id);
            Response response = testHandler.execute("/stock/insert", parameterMap);
            System.out.println(response.getResponseMessage());
        }
    }
}
