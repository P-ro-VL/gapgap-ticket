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
import vn.hoangshitposting.gapgapticket.dto.request.BuyMerchRequest;
import vn.hoangshitposting.gapgapticket.dto.request.GalleryInvitationRequest;
import vn.hoangshitposting.gapgapticket.dto.request.MerchMetaRequest;
import vn.hoangshitposting.gapgapticket.dto.request.SendEmailRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class EmailService {

    // Gmail SMTP settings
    private static String host = "smtp.gmail.com";
    private static int port = 587;

    public static void sendConfirmTicketEmail(String toEmail, SendEmailRequest request) {
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
            String content = Files.readString(Path.of("confirm_ticket.html"));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendConfirmMerchEmail(String toEmail, BuyMerchRequest request) {
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
            message.setSubject("Merch “Có Cần Phải Có Lý Không?” xác nhận thông tin Pre-order!”");

            // Set HTML content
            String content = Files.readString(Path.of("confirm_ticket.html"));
            content = content.replaceAll("\\{name\\}", request.getFullName());
            content = content.replaceAll("\\{email\\}", request.getEmail());
            content = content.replaceAll("\\{phone\\}", request.getPhoneNumber());
            content = content.replaceAll("\\{address\\}", request.getAddress());
            content = content.replaceAll("\\{shippingFee\\}", request.getShippingFee() + "");

            String rowTemplate = """
                    <tr>
                            <td>{merchName}</td>
                            <td>{amount}</td>
                            <td>{price}</td>
                  </tr>
                    """;
            StringBuilder row = new StringBuilder();
            for(int i = 0; i < request.getMerches().size(); i++) {
                MerchMetaRequest merch = request.getMerches().get(i);


                row.append(rowTemplate
                        .replaceAll("\\{merchName\\}", merch.getName())
                        .replaceAll("\\{price\\}", formatVND(merch.getPrice() * merch.getAmount()))
                        .replaceAll("\\{amount\\}", merch.getAmount() + "")
                );
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendGalleryInvitationEmail(String toEmail, GalleryInvitationRequest request) {
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
            message.setSubject("Thư mời tham dự triển lãm “Gấp Gap””");

            // Set HTML content
            String content = Files.readString(Path.of("gallery_invitation.html"));
            content = content.replaceAll("\\{name\\}", request.getFullName());

            // Create the multipart email
            MimeMultipart multipart = new MimeMultipart("related");

            // 1. HTML part
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(content, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            // 2. Image part (inline)
            attachImage(multipart, "background.png", "backgroundImage");
            attachImage(multipart, "typo-header.png", "typoHeaderImage");
            attachImage(multipart, "typo-footer.png", "typoFooterImage");

            // Set the multipart content to message
            message.setContent(multipart);

            // Send email
            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void attachImage(MimeMultipart multipart, String filePath, String contentId) throws Exception {
        MimeBodyPart imagePart = new MimeBodyPart();
        DataSource fds = new FileDataSource(filePath);
        imagePart.setDataHandler(new DataHandler(fds));
        imagePart.setHeader("Content-ID", "<" + contentId + ">");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(imagePart);
    }

    private static String formatVND(int amount) {
        Locale vietnamLocale = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnamLocale);

        return currencyFormatter.format(amount);
    }

}