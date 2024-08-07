package com.few.email.service.article

import com.few.email.service.SendAEmailTestSpec
import com.few.email.service.article.dto.Content
import com.few.email.service.article.dto.SendArticleEmailArgs
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

class SendArticleEmailServiceTest : SendAEmailTestSpec() {

    @Autowired
    private lateinit var sendArticleEmailService: SendArticleEmailService

    @Test
    @Disabled
    fun `아티클 이메일 전송 테스트`() {
        // given
        val args = SendArticleEmailArgs(
            to = "test@gmail.com",
            subject = "테스트" + LocalDateTime.now(),
            content = Content(
                articleLink = URL("https://www.google.com"),
                currentDate = LocalDate.now(),
                category = "경제",
                articleDay = 1,
                articleTitle = "제목",
                writerName = "작성자",
                writerLink = URL("https://www.google.com"),
                articleContent = "내용",
                problemLink = URL("https://www.google.com"),
                unsubscribeLink = URL("https://www.google.com")
            ),
            template = "article"
        )

        // when
        val result = sendArticleEmailService.send(args)

        // then
        // check Mailtrap
    }

    fun getStyle(): String {
        return "  * {\n" +
            "      box-sizing: border-box;\n" +
            "    }\n" +
            "\n" +
            "    body {\n" +
            "      margin: 0;\n" +
            "      padding: 0;\n" +
            "    }\n" +
            "\n" +
            "    a[x-apple-data-detectors] {\n" +
            "      color: inherit !important;\n" +
            "      text-decoration: inherit !important;\n" +
            "    }\n" +
            "\n" +
            "    #MessageViewBody a {\n" +
            "      color: inherit;\n" +
            "      text-decoration: none;\n" +
            "    }\n" +
            "\n" +
            "    p {\n" +
            "      line-height: inherit\n" +
            "    }\n" +
            "\n" +
            "    .desktop_hide,\n" +
            "    .desktop_hide table {\n" +
            "      mso-hide: all;\n" +
            "      display: none;\n" +
            "      max-height: 0px;\n" +
            "      overflow: hidden;\n" +
            "    }\n" +
            "\n" +
            "    .image_block img+div {\n" +
            "      display: none;\n" +
            "    }\n" +
            "\n" +
            "    @media (max-width:720px) {\n" +
            "      .desktop_hide table.icons-inner {\n" +
            "        display: inline-block !important;\n" +
            "      }\n" +
            "\n" +
            "      .icons-inner {\n" +
            "        text-align: center;\n" +
            "      }\n" +
            "\n" +
            "      .icons-inner td {\n" +
            "        margin: 0 auto;\n" +
            "      }\n" +
            "\n" +
            "      .image_block div.fullWidth {\n" +
            "        max-width: 100% !important;\n" +
            "      }\n" +
            "\n" +
            "      .mobile_hide {\n" +
            "        display: none;\n" +
            "      }\n" +
            "\n" +
            "      .row-content {\n" +
            "        width: 100% !important;\n" +
            "      }\n" +
            "\n" +
            "      .stack .column {\n" +
            "        width: 100%;\n" +
            "        display: block;\n" +
            "      }\n" +
            "\n" +
            "      .mobile_hide {\n" +
            "        min-height: 0;\n" +
            "        max-height: 0;\n" +
            "        max-width: 0;\n" +
            "        overflow: hidden;\n" +
            "        font-size: 0px;\n" +
            "      }\n" +
            "\n" +
            "      .desktop_hide,\n" +
            "      .desktop_hide table {\n" +
            "        display: table !important;\n" +
            "        max-height: none !important;\n" +
            "      }\n" +
            "    }"
    }
}