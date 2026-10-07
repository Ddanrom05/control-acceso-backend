package com.example.control_acceso.controller;

import com.example.control_acceso.model.Usuario;
import com.example.control_acceso.service.AccesoService;
import com.example.control_acceso.model.LogAcceso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AccesoController {

    private final AccesoService servicio;

    public AccesoController(AccesoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/acceso/{uid}")
    public ResponseEntity<?> verificarAcceso(@PathVariable String uid) {

        return servicio.verificarAcceso(uid)
                .map(usuario ->
                    ResponseEntity.ok(
                        Map.of(
                            "autorizado", true,
                            "uid", usuario.getUid(),
                            "nombre", usuario.getNombre()
                        )
                    )
                )
                .orElseGet(() ->
                    ResponseEntity.ok(
                        Map.of(
                            "autorizado", false,
                            "uid", uid
                        )
                    )
                );
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> agregarUsuario(
            @RequestBody Usuario usuario) {

        try {

            Usuario nuevo = servicio.agregarUsuario(
                    usuario.getUid(),
                    usuario.getNombre()
            );

            return ResponseEntity.ok(
                Map.of(
                    "exito", true,
                    "uid", nuevo.getUid(),
                    "mensaje", "Usuario registrado"
                )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(
                        Map.of(
                            "exito", false,
                            "mensaje",
                            "El UID ya existe o los datos son inválidos"
                        )
                    );
        }
    }

    @DeleteMapping("/usuarios/{uid}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable String uid) {

        boolean eliminado = servicio.eliminarUsuario(uid);

        if (eliminado) {

            return ResponseEntity.ok(
                Map.of(
                    "eliminado", true,
                    "uid", uid
                )
            );
        }

        return ResponseEntity.ok(
            Map.of(
                "eliminado", false,
                "uid", uid
            )
        );
    }

    @GetMapping("/usuarios")
    public List<Map<String, Object>> obtenerUsuarios() {

        return servicio.obtenerUsuarios()
                .stream()
                .map(usuario ->
                    Map.<String, Object>of(
                        "uid", usuario.getUid(),
                        "nombre", usuario.getNombre()
                    )
                )
                .toList();
    }

    @GetMapping("/logs")
    public List<LogAcceso> obtenerLogs() {

        return servicio.obtenerLogs();
    }
}