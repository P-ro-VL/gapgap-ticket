package vn.hoangshitposting.gapgapticket.service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import vn.hoangshitposting.gapgapticket.dto.request.SendEmailRequest;

import java.util.*;

@Service
@AllArgsConstructor
public class EmailService {

    public static String EMAIL_TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <style>
                  table {
                    border-collapse: collapse;
                    width: 100%;
                  }
                       \s
                  td,
                  th {
                    border: 1px solid #dddddd;
                    text-align: left;
                    padding: 8px;
                  }
                       \s
                  tr:nth-child(even) {
                    background-color: #dddddd;
                  }
                </style>
              </head>
              <body>
                <img src='cid:headerImage' style='width:100%; max-width:800px; margin-bottom: 16px;'>
                <br/>
                <span class="display: flex">
                  Cảm ơn
                  <span style="color: red; font-weight: bold">{name}</span>
                </span>
                <p style="margin-bottom: 0">
                  Cảm ơn bạn đã đặt vé tham gia Cover Show "CÓ CẦN PHẢI CÓ LÝ KHÔNG?" SÀI GÒN. Chúng mình sẽ tiến hành xác nhận thanh toán và gửi vé đến bạn trong vòng 48 giờ kể từ khi nhận được thanh toán thành công.<br/><br/>
                  Đây là thông tin đơn hàng của bạn. Bạn kiểm tra kĩ và phản hồi lại chúng mình nếu có sai sót nhé^^
                </p>
                <p style="font-weight: bold; color: orange; margin: 0; font-style: italic">
                  Họ và tên:
                       \s
                  <span style="color: black">{name}</span>
                </p>
                <p style="font-weight: bold; color: orange; margin: 0; font-style: italic">
                  Số điện thoại:
                       \s
                  <span style="color: black">{phone}</span>
                </p>
                <p style="font-weight: bold; color: orange; margin: 0; font-style: italic">
                  Email:
                       \s
                  <span style="color: black">{email}</span>
                </p>
                <p style="font-weight: bold; color: orange; margin: 0; font-style: italic">
                  Số lượng vé:
                       \s
                  <span style="color: black">{email}</span>
                </p>
                <p
                  style="
                    font-weight: bold;
                    color: orange;
                    margin: 0 0 16px 0;
                    font-style: italic;
                  "
                >
                  Tổng giá trị đơn hàng:
                       \s
                  <span style="color: black">{totalPrice}</span>
                </p>
                       \s
                <p style="font-weight: bold">
                  Một số lưu ý về quy định đổi/trả:
                  <ul style="list-style-type: '-     ';">
                    <li>
                        Chương trình không áp dụng đổi/trả sau khi đã xác nhận thanh toán thành công vì bất kỳ lý do gì. Mọi thắc mắc, xin hãy trao đổi trực tiếp với chúng tớ qua Email và Fanpage Hoangshitposting nha
                    </li>
                  </ul>
                </p>
                       \s
                <p style="margin-top: 20px">
                  🐟 Cover Show "CÓ CẦN PHẢI CÓ LÝ KHÔNG?" <br />
                  ► Thời gian: 19h - 22h 13/09/2025 <br />
                  ► Địa điểm: Golden Bird's Event Space - 142 Đường Trần Não, Phường Bình An, Quận 2, Thành phố Thủ Đức, Hồ Chí Minh<br /><br/>Nghe nhạc Cá Hồi Hoang và đợi chúng mình nha 🐟🐟🐟
                </p>
                       \s
                <hr style="background-color: #BDC1C6;"/>
                       \s
                <p style="font-weight: bold;">
                    Hoang Shitposting<br/>
                    Liên hệ:<br/>
                    <span style="font-weight: 500 !important;">
                        - Fanpage: <a href="https://www.facebook.com/hoangshitposting">Hoangshitposting</a><br/>
                        - Email: <a href="mailto:hoangshitposting@gmail.com">hoangshitposting@gmail.com</a><br/>
                        - Hotline: <span style="color: black;">0968023065 (Hải Yến)</span>
                    </span>
                </p>
              </body>
            </html>
            """;

    public static void sendHtmlEmail(String toEmail, SendEmailRequest request) {
        // Gmail SMTP settings
        String host = "smtp.gmail.com";
        int port = 587;

        // Configure SMTP properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        // Authenticate using Gmail credentials (App Password)
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hoangshitposting@gmail.com", "rhrb ctpl zqrw rmlo");
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("hoangshitposting@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Đặt vé thành công Cover Show “Có cần phải có lý không?”");

            // Set HTML content
            String content = EMAIL_TEMPLATE;
            content = content.replaceAll("\\{name\\}", request.getPurchaseRequest().getName());
            content = content.replaceAll("\\{email\\}", request.getPurchaseRequest().getEmail());
            content = content.replaceAll("\\{phone\\}", request.getPurchaseRequest().getPhoneNumber());
            content = content.replaceAll("\\{ticketName\\}", request.getTicket().getName());
            content = content.replaceAll("\\{amount\\}", request.getAmount() + "");
            content = content.replaceAll("\\{totalPrice\\}", (request.getAmount() * request.getTicket().getPrice()) + "");


            String rowTemplate = """
                    <tr>
                            <td>Covershow “Có Cần Phải Có Lý Không?” {ticketType}</td>
                            <td>{code}</td>
                            <td><img src="https://api.qrserver.com/v1/create-qr-code/?data={code}&size=150x150&margin=0"/></td>
                  </tr>
                    """;
            StringBuilder row = new StringBuilder();
            for(int i = 0; i < request.getAmount(); i++) {
                String code = request.getPurchases().get(i).getCode();

                row.append(rowTemplate.replaceAll("\\{ticketType\\}", request.getTicket().getName())
                        .replaceAll("\\{code\\}", code));
            }

            content = content.replaceAll("\\{rows\\}", row.toString());

            // Create the multipart email
            MimeMultipart multipart = new MimeMultipart("related");

            // 1. HTML part
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // 2. Image part (inline)
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource fds = new FileDataSource("header.png");
            imagePart.setDataHandler(new DataHandler(fds));
            imagePart.setFileName("Gấp Gap");
            imagePart.setHeader("Content-ID", "<headerImage>");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            multipart.addBodyPart(imagePart);

            // Set the multipart content to message
            message.setContent(multipart);

            // Send email
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}