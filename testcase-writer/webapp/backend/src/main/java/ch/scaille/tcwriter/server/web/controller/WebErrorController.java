package ch.scaille.tcwriter.server.web.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.autoconfigure.error.AbstractErrorController;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import ch.scaille.tcwriter.server.exceptions.WebRTException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/error")
@NullMarked
public class WebErrorController extends AbstractErrorController {

    private static final Locale LOCALE = Locale.US;

    private record WebErrorViewResolver(ApplicationContext context) implements ErrorViewResolver {

        @Override
        public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
            final var resource = this.context.getResource("classpath:templates/errorPage.html");
            return new ModelAndView(new HtmlResourceView(resource), model);
        }

    }

    /**
     * {@link View} backed by an HTML resource.
     */
    private record HtmlResourceView(Resource resource) implements View {

        @Override
        public String getContentType() {
            return MediaType.TEXT_HTML_VALUE;
        }

        @Override
        public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
                throws Exception {
            response.setContentType(getContentType());
            final var dto = (ExceptionDto) Objects.requireNonNull(model, "No model provided").get("dto");
            try (var rsrcStream = new BufferedReader(
                    new InputStreamReader(this.resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = rsrcStream.readLine()) != null) {
                    response.getWriter()
                            .write(line.replace("{code}", dto.getCode())
                                    .replace("{arguments}", Arrays.toString(dto.getArguments()))
                                    .replace("{message}", Objects.requireNonNullElse(dto.getMessage(), ""))
                                    .replace("{trace}", dto.getTrace())
                                    .replace("{httpStatusValue}", Integer.toString(dto.getStatus().value()))
                                    .replace("{httpStatusCode}", dto.getStatus().name()));
                }
            }
        }

    }

    private final MessageSource messageSource;

    public WebErrorController(ErrorAttributes errorAttributes, ApplicationContext context,
                              MessageSource messageSource) {
        super(errorAttributes, Collections.singletonList(new WebErrorViewResolver(context)));
        this.messageSource = messageSource;
    }

    public static class ExceptionDto {
        private final String code;
        private final Object[] arguments;
        private final HttpStatus status;
        @Nullable
        private String message = null;
        private String trace = "";

        public ExceptionDto(String code, final Object @Nullable [] nullableArguments, HttpStatus status) {
            super();
            this.code = code;
            this.arguments = Objects.requireNonNullElse(nullableArguments, new Object[0]);
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public void setMessage(@Nullable String text) {
            this.message = text;
        }

        @Nullable
        public String getMessage() {
            return message;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }

        public HttpStatus getStatus() {
            return status;
        }

    }

    private ExceptionDto toDto(HttpServletRequest request) {
        final var exc = (Exception) request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
        final var status = getStatus(request);
        ExceptionDto dto;
        String defaultText;
        if (exc instanceof WebRTException webRTexc) {
            dto = new ExceptionDto(webRTexc.getDetailMessageCode(), webRTexc.getDetailMessageArguments(), status);
            defaultText = webRTexc.getDetailMessageCode();
        } else if (exc != null) {
            dto = new ExceptionDto("exception." + exc.getClass().getName(), new Object[]{exc.getMessage()}, status);
            defaultText = messageSource.getMessage("error.exception",
                    new Object[]{exc.getClass().getName(), exc.getMessage()}, "error.exception", LOCALE);
        } else {
            dto = new ExceptionDto("exception." + status.name(), new Object[]{status.getReasonPhrase()}, status);
            defaultText = messageSource.getMessage("error.status", new Object[]{status}, "error.status", LOCALE);
        }
        dto.setMessage(messageSource.getMessage(dto.getCode(), dto.getArguments(), defaultText, LOCALE));
        var trace = (String) getErrorAttributes(request,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)).get("trace");
        if (trace != null) {
            dto.setTrace(trace);
        }

        return dto;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @Nullable
    public ModelAndView handleErrorHtml(HttpServletRequest request, HttpServletResponse response,
                                        Map<String, Object> model) {
        final var dto = toDto(request);
        model.put("dto", dto);
        return super.resolveErrorView(request, response, getStatus(request), model);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ExceptionDto handleErrorJsonGet(HttpServletRequest request) {
        return toDto(request);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ExceptionDto handleErrorJsonPost(HttpServletRequest request) {
        return toDto(request);
    }

}
