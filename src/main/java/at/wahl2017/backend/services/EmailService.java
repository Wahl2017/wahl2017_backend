package at.wahl2017.backend.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private TemplateEngine emailTemplateEngine;
	
	@Value("${wahl2017.email.addbcc}")
	private String additionalBcc;
	
	@Async
	public void send(String from, String replyTo, String recipients[], String cc[], String bcc[], String templateName, String subject, Map<String, Object> variables) throws MessagingException {
		final Context context = this.prepareContext(variables);
		final String htmlContent = this.emailTemplateEngine.process(templateName, context);
		final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setSubject(subject);
		message.setFrom(from);
		if(!StringUtils.isEmpty(replyTo)) {
			message.setReplyTo(replyTo);
		}
		message.setTo(recipients);
		if(cc != null) {
			message.setCc(cc);
		}
		String additionalBcc = this.additionalBcc;
		if(bcc != null) {
			if(!StringUtils.isEmpty(additionalBcc)) {
				ArrayList<String> bccList = new ArrayList<>(bcc.length + 1);
				bccList.addAll(Arrays.asList(bcc));
				bccList.add(additionalBcc);
				bcc = bccList.toArray(new String[bccList.size()]);
			}
			message.setBcc(bcc);
		}else if(!StringUtils.isEmpty(additionalBcc)) {
			message.setBcc(additionalBcc);
		}
		message.setText(htmlContent, true);
		
		this.javaMailSender.send(mimeMessage);
	}
	
	private Context prepareContext(Map<String, Object> variables) {
		final Context context = new Context();
		if(variables != null) {
			context.setVariables(variables);
		}
		return context;
	}
	
}
