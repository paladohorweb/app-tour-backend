package com.jgm.paladohorweb.tour.repository;

import com.jgm.paladohorweb.tour.entity.Rol;
import com.jgm.paladohorweb.tour.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRol(Rol rol);
}
