package com.mobiauto.backend.interview.jobs;

import com.mobiauto.backend.interview.config.NivelAcessoConfig;
import com.mobiauto.backend.interview.model.Role;
import com.mobiauto.backend.interview.model.Usuario;
import com.mobiauto.backend.interview.service.RoleService;
import com.mobiauto.backend.interview.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarEntidadesJob {

    private final RoleService roleService;
    private final UsuarioService usuarioService;

    public void inserirRolesNoBanco() {
        //Organização do nível de autoridade. O menor ID terá mais permissões.
        roleService.save(Role.builder().id(1L).name(NivelAcessoConfig.NIVEL_ADMINISTRADOR).build());
        roleService.save(Role.builder().id(2L).name(NivelAcessoConfig.NIVEL_PROPRIETARIO).build());
        roleService.save(Role.builder().id(3L).name(NivelAcessoConfig.NIVEL_GERENTE).build());
        roleService.save(Role.builder().id(4L).name(NivelAcessoConfig.NIVEL_ASSISTENTE).build());
    }

    public void inserirAdminNoBanco() {
        usuarioService.save(Usuario.builder()
                        .nome("Administrador")
                        .email("admin@email.com")
                        .senha("9090")
                        .roles(roleService.findAll())
                .build());
    }

}