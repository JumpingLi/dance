package com.champion.dance;

import com.champion.dance.domain.entity.MemberCard;
import com.champion.dance.domain.enumeration.CardType;
import com.champion.dance.domain.enumeration.NameType;
import com.champion.dance.service.MemberCardService;
import com.champion.dance.service.TeacherService;
import com.champion.dance.utils.DateTimeUtil;
import com.champion.wechat.constant.ConstantWeChat;
import com.champion.wechat.entity.AccessToken;
import com.champion.wechat.util.WeixinUtil;
import com.csvreader.CsvReader;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/27 10:34
 * Description：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Autowired
    private MemberCardService memberCardService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8"), "/*")
                .alwaysExpect(status().isOk())
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .build();
    }

    @Test
    public void insertData() throws IOException {
        CsvReader r = new CsvReader("D:\\data.csv", ',', Charset.forName("UTF-8"));
        while (r.readRecord()) {
            String[] arr = r.getValues();
            for(String s:arr){
                System.out.println(s);
            }
            System.out.println("------这是分割线-----");
        }
    }

    @Test
    public void serviceTest(){
//        Map<String,Teacher> teacherMap = teacherService.findAllTeacher(Arrays.asList("b7651ef4-6552-4cca-84f6-6dad3b13d401"));
//        Teacher teacher = teacherMap.get("b7651ef4-6552-4cca-84f6-6dad3b13d401");
//        System.out.println(teacher.getName());
//        LocalDate localDate = DateTimeUtil.parseDate("2018-02-27");
//        long seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
//                LocalDateTime.of(localDate.plusDays(2).getYear(),localDate.plusDays(2).getMonth(),localDate.plusDays(2).getDayOfMonth(),0,0));
//        System.out.println(seconds/60);
//        Teacher teacher = (Teacher) redisTemplate.opsForValue().get("jumping");

//        System.out.println(teacher.getName());
//        AccessToken accessToken = WeixinUtil.getAccessToken(ConstantWeChat.APPID,ConstantWeChat.APPSECRET);
//        System.out.println(accessToken.getToken() + "" + accessToken.getExpiresIn());
//        redisTemplate.opsForValue().set(ConstantWeChat.APPID,accessToken.getToken(),accessToken.getExpiresIn()-5, TimeUnit.SECONDS);
//        String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYMMdd"));
//        System.out.println(System.getProperty("sun.arch.data.model"));
        long days = Duration.between(LocalDateTime.now().plusDays(-10),LocalDateTime.now()).toDays();
        System.out.println(days);
    }

    @Test
    public void testGet() throws Exception {
//        String url = "/member/info";
//        String url = "/member/courses?page=1&limit=10";
//        String url = "/member/cards";
//        String url = "/member-card/buy-info";
        String url = "/member/card/get/1Q91JQ";
//        String url = "/course/detail/009a3f7e-8fd2-43c0-bafe-56117487cdb5/2018-03-01";//课程详情
//        String url = "/course/comments/009a3f7e-8fd2-43c0-bafe-56117487cdb5";//课程评价列表
//        String url = "/courses?localDate=2018-03-07";//课程列表
//        String url = "/course/subscribe?courseId=009a3f7e-8fd2-43c0-bafe-56117487cdb5&subDate=2018-03-01";//预约
//        String url = "/course/cancel?subscribeId=2";//取消预约
//        String url = "/member/auth-code/18512196875";
        String responseString = mockMvc.perform(get(url)
                .requestAttr("sessionId", "otGJPwLwQ5mmI-VV7epeBb-nQB48")
//                .header("sessionId","123")
        )
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    @Test
    public void testPost() throws Exception{
//        String url = "/comment/submit";
//        String url = "/member/register";
        String url = "/member/card/record";
//        String json = "{\n" +
//                "\"content\": \"超级棒！！！\",\n" +
//                "\"startLevel\": \"FIVE\",\n" +
//                "\"courseId\": \"009a3f7e-8fd2-43c0-bafe-56117487cdb5\",\n" +
//                "\"courseDate\": \"2018-03-01\"\n" +
//                "}";
//        String json = "{\n" +
//                "\"openId\": \"321\",\n" +
//                "\"authCode\": \"921574\",\n" +
//                "\"danceId\": \"8f863435-aaa4-4d5d-a2df-95cb2cfebd96\",\n" +
//                "\"nickname\": \"jumping\",\n" +
//                "\"realName\": \"champion\",\n" +
//                "\"mobile\": \"18512196875\",\n" +
//                "\"age\": \"25\",\n" +
//                "\"birthday\": \"2018-03-01\",\n" +
//                "\"danceAge\": \"5\",\n" +
//                "\"city\": \"南京市\",\n" +
//                "\"district\": \"建邺区\",\n" +
//                "\"province\": \"江苏省\"\n" +
//                "}";
        String json = "{\n" +
                "\"nameType\": \"TIMES_30\",\n" +
                "\"mobile\": \"18512526587\",\n" +
                "\"amount\": \"1288\",\n" +
                "\"counselor\": \"老大\",\n" +
                "\"operator\": \"大佬\",\n" +
                "\"chargeType\": \"CASH\"\n" +
                "}";
        String responseString = mockMvc.perform(post(url, json)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json.getBytes("UTF-8"))
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .requestAttr("sessionId", "123"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }
}
