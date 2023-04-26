package com.example.springsecurityapplication.config;

// import com.example.springsecurityapplication.security.AuthenticationProvider;
import com.example.springsecurityapplication.services.PersonDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfig{
//    private final AuthenticationProvider authenticationProvider;
//
//    public SecurityConfig(AuthenticationProvider authenticationProvider) {
//        this.authenticationProvider = authenticationProvider;
//    }

    private final PersonDetailsService personDetailsService;
    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Конфигурируем работу Спринг Сесурити

//        http.csrf().disable() // отключаем защиту от межсайтовой подделки запросов. Так делать не надо
        http

                .authorizeHttpRequests() // Указываем, что все страницы должны быть защищены аутентификацией

                .requestMatchers("/admin").hasRole("ADMIN") //С помощью этой настройки прописываем, что к "/admin" обращаемся только если роль админа
                .requestMatchers("/authentication", "/error", "/registration", "/resources/**", "static/**", "css/**", "js/**", "/img/**", "/product", "/product/info/{id}", "/product/search").permitAll() // Указываем, что неаутентифицированные пользователи могут зайти на страницу аутентификации и на объект ошибки
                // с помощью permitAll указываем, что неаутентифицированные пользователи могут заходить на перечисленые страницы

                .anyRequest().hasAnyRole("USER", "ADMIN")

//                .anyRequest().authenticated() // указываем, что  для всех остальныъ страниц необходимо вызвать метод authenticated(), который открывает форму аутентификации

                .and() // указываем, что дальше настраивается аутентификация и соединяем её с настройкой доступа

                .formLogin().loginPage("/authentication") // указываем, какой url-запрос будет отправляться при заходе на защищенные страницы

                .loginProcessingUrl("/process_login") // указываем, на какой url-адрес будут отправляться данные формы. Нам уже не нужно будет создавать метод в контроллере и обрабатывать данные с формы. Мы задали url, который используется по-умолчанию для обработки формы аутентификации по стредствам Spring Security. Spring Security будет ждать объект с формы аутентификации и затем сверять логин и пароль с данными в БД

                .defaultSuccessUrl("/person_account", true) // Указываем, на какой url необходимо направить пользователя после успешной аутентификации. Вторым аргументом указывается true, чтобы перенаправление шло в любом случае после успешной аутентификации

                .failureUrl("/authentication?error") // Указываем, куда необходимо перенаправить пользователя при проваленной аутентификации. В запрос будет передан объект error, который будет проверяться на форме и приналичии данного объекта в запросе выводится сообщение "Неправильный логин или пароль"
                .and() // Указываем, что будет ещё блок
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication"); // Указываем, по какому адресу будет происходить выход из аккаунта в сессии, а потом по какому адресу будет переход. Спринг сам это совершит

        return http.build();
    }


    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }


    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncode());
    }
}

