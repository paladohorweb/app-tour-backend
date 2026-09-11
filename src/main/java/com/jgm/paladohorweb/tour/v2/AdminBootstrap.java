package com.jgm.paladohorweb.tour.v2;

import com.jgm.paladohorweb.tour.entity.*;
import com.jgm.paladohorweb.tour.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {
  final UsuarioRepository users;
  final PasswordEncoder encoder;

  @Value("${BOOTSTRAP_ADMIN_EMAIL:}")
  String email;

  @Value("${BOOTSTRAP_ADMIN_PASSWORD:}")
  String password;

  public void run(ApplicationArguments args) {
    if (email.isBlank()) return;
    if (password.length() < 16 || password.length() > 72)
      throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD requiere de 16 a 72 caracteres");
    if (users.findByEmail(email).isPresent()) return;
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setNombre("Administración");
    u.setRol(Rol.ROLE_ADMIN);
    u.setPassword(encoder.encode(password));
    u.setActivo(true);
    users.save(u);
  }
}
