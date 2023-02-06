package camp.nextstep.config;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpHeaderFilter extends OncePerRequestFilter {

    private static final Map<String, List<String>> responseHeaders = Map.of(
            "Cache-Control", List.of("no-store", "no-cache", "must-revalidate")
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        responseHeaders.forEach((key, value) -> response.addHeader(key, toHeaderValue(value)));

        filterChain.doFilter(request, response);
    }

    private String toHeaderValue(List<String> values) {
        return String.join(", ", values);
    }
}
