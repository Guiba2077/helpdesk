package br.com.edu.senai.fatesg.ads4.helpdesk_back.business.usuarios;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("businessUsuarioJpaRepository")
public interface UsuarioJpaRepository extends JpaRepository<UsuarioModel, Long> {

    List<UsuarioModel> findByAtivoTrueOrderByIdDesc();

    List<UsuarioModel> findAllByOrderByIdDesc();

    Optional<UsuarioModel> findByEmailIgnoreCaseAndAtivoTrue(String email);

    @Query(value = """
            SELECT * FROM app.usuario
             WHERE LOWER(email) = LOWER(:email)
               AND ativo = true
               AND senha_hash = crypt(:senha, senha_hash)
            """, nativeQuery = true)
    Optional<UsuarioModel> autenticarComPgcrypto(@Param("email") String email, @Param("senha") String senha);

    @Query("SELECT u FROM UsuarioModel u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY u.nome")
    List<UsuarioModel> buscarPorNome(@Param("nome") String nome);

    @Modifying
    @Query(value = """
            INSERT INTO app.usuario (nome, email, senha_hash, telefone, setor, perfil, ativo, criado_em, atualizado_em)
            VALUES (:nome, :email, crypt(:senha, gen_salt('bf')), :telefone, :setor, :perfil, true, now(), now())
            RETURNING id
            """, nativeQuery = true)
    Long inserirComPgcrypto(
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("senha") String senha,
            @Param("telefone") String telefone,
            @Param("setor") String setor,
            @Param("perfil") String perfil);

    @Modifying
    @Query("UPDATE UsuarioModel u SET u.ativo = CASE WHEN u.ativo = true THEN false ELSE true END WHERE u.id = :id")
    void alternarStatus(@Param("id") Long id);

    @Modifying
    @Query("UPDATE UsuarioModel u SET u.ativo = false WHERE u.id = :id")
    void inativar(@Param("id") Long id);
}

