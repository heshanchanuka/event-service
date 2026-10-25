package com.eventbooking.event_service.config;


// Spring Boot වලට අවශ්‍ය Classes Import කරගන්නවා
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * @Configuration කියන්නේ Spring Boot Start වෙද්දීම මේ ක්ලාස් එක කියවලා
 * මේකේ තියෙන Settings මුළු ප්‍රොජෙක්ට් එකටම අදාළ කරගන්නවා කියන එකයි.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /*
     * WebMvcConfigurer එකේ තියෙන addCorsMappings කියන ෆන්ක්ෂන් එක අපි මෙතන Override කරලා
     * අපිට ඕන විදියට නීති ටික හදාගන්නවා.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // 1. registry.addMapping("/**")
        // "/**" කියන්නේ අපේ Backend එකේ තියෙන ඕනෑම API Endpoint එකකට (උදා: /api/bookings, /api/seats) මේ නීතිය අදාළයි.
        registry.addMapping("/**")

                // 2. allowedOrigins("http://localhost:5173")
                // මේක තමයි වැදගත්ම කෑල්ල! අපේ React Frontend එක දුවන Port එක (5173) මෙතන දෙනවා.
                // ඒ කියන්නේ "මේ තැනින් එන අයට විතරක් ගේට්ටුව අරින්න" කියලා.
                .allowedOrigins("http://localhost:5173")

                // 3. allowedMethods(...)
                // React එකෙන් Backend එකට එවන HTTP Methods මොනවද කියලා මෙතනින් අවසර දෙනවා.
                // GET (ඩේටා ගන්න), POST (අලුතින් දාන්න), PUT/PATCH (අප්ඩේට් කරන්න), DELETE (මකන්න)
                // OPTIONS කියන්නේ බ්‍රවුසර් එකෙන් CORS චෙක් කරන්න යවන "Pre-flight" request එක (මේක අනිවාර්යයි).
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // 4. allowedHeaders("*")
                // Frontend එකෙන් එවන ඕනෑම Header එකක් (උදා: Content-Type, Authorization tokens) භාරගන්නවා.
                .allowedHeaders("*")

                // 5. allowCredentials(true)
                // Cookies, Authorization Headers වගේ දේවල් Frontend එකේ ඉඳන් එවනවා නම් මේක true වෙන්නම ඕනේ.
                .allowCredentials(true);
    }
}