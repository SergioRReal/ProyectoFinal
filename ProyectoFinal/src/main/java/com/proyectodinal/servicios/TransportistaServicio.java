/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyectodinal.servicios;

import com.proyectofinal.entidades.Camion;
import com.proyectofinal.entidades.Foto;
import com.proyectofinal.entidades.Transportista;
import com.proyectofinal.entidades.Usuario;
import com.proyectofinal.errores.ErroresServicio;
import com.proyectofinal.repositorios.RepositorioTransportista;
import com.proyectofinal.repositorios.RepositorioUsuario;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class TransportistaServicio {
 
    @Autowired
    private RepositorioTransportista repositorioTransportista;
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    @Autowired
    private NotificacionDeServicio notificacionServicio;

    @Transactional
    public void crearTransportista(String nombre, String apellido, String mail, String password, Foto foto, String zona, Integer telefono, Camion camion, Integer cantidadViajes) throws ErroresServicio {
        validarTransportista(nombre, apellido, mail, password, foto, zona, telefono, camion);
        Optional<Usuario> respuesta = repositorioUsuario.buscarPorMail(mail);
        if (respuesta.isPresent()) {
            throw new ErroresServicio("El mail ya esta utilizado");

        } else {
            //se crea la entidad transportista
            Transportista transportista = new Transportista();
            //se setea con todos los atributos
            transportista.setNombre(nombre);
            transportista.setApellido(apellido);
            transportista.setMail(mail);
            String encriptada = new BCryptPasswordEncoder().encode(password);
            transportista.setPassword(encriptada);
            transportista.setFoto(foto);
            transportista.setZona(zona);
            transportista.setTelefono(telefono);
            transportista.setCamion(camion);
            transportista.setCantidadViajes(0);
            transportista.setValoracion(0);
            transportista.setEstado(true);
            //se envia notificacion que lo realizo correctamente
            notificacionServicio.enviar("TEXTO DE BIENVENIDA", "NOMBRE DE LA PAGINA", transportista.getMail());
            //se guarda en el repositorio o base de datos
            repositorioTransportista.save(transportista);
        }
    }

    @Transactional
    public void modificarUsuario(String id, String nombre, String apellido, String mail, String password, Foto foto, String zona, Integer telefono, Camion camion, double valoracion, Integer cantidadViajes) throws ErroresServicio {
        //se busca si existe el transportista en la base de datos
        Optional<Transportista> respuesta = repositorioTransportista.findById(id);
        if (respuesta.isPresent()) {
            Transportista transportista = respuesta.get();
            transportista.setNombre(nombre);
            transportista.setApellido(apellido);
            transportista.setMail(mail);
            String encriptada = new BCryptPasswordEncoder().encode(password);
            transportista.setPassword(encriptada);
            transportista.setFoto(foto);
            transportista.setZona(zona);
            transportista.setTelefono(telefono);
            transportista.setCamion(camion);
            //no debe estar la valoracion y cantidad de viajes porque eso no lo deberia poder cambiar el transportista
            transportista.setValoracion(valoracion);
            transportista.setCantidadViajes(cantidadViajes);
            notificacionServicio.enviar("TEXTO DE MODIFICACION DE CREDENCIALES", "NOMBRE DE LA PAGINA", transportista.getMail());
            repositorioTransportista.save(transportista);
        } else {
            throw new ErroresServicio("No se encontro el usuario solicitado");
        }
    }

    public void validarTransportista(String nombre, String apellido, String mail, String password, Foto foto, String zona, Integer telefono, Camion camion) throws ErroresServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErroresServicio("Debe ingresar un nombre");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErroresServicio("Debe ingresar un apellido");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErroresServicio("Debe ingresar un mail");
        }
        if (telefono == null) {
            throw new ErroresServicio("Debe ingresar un telefono ");
        }
        if (password == null || password.isEmpty()) {
            throw new ErroresServicio("Debe ingresar una contraseña");
        }
        if (zona == null || zona.isEmpty()) {
            throw new ErroresServicio("Debe ingresar una zona");
        }
        if (foto == null) {
            throw new ErroresServicio("Debe ingresar una foto");
        }
        if (camion == null) {
            throw new ErroresServicio("Debe ingresar un camion");
        }

    }

    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Optional<Usuario> usuario = repositorioUsuario.buscarPorMail(mail);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario.get());
            User user = new User(usuario.get().getMail(), usuario.get().getPassword(), permisos);
            return user;

        } else {
            return null;
        }
    }
}
