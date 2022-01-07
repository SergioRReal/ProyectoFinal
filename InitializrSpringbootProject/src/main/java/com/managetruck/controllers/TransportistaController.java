package com.managetruck.controllers;

import com.managetruck.entidades.Camion;
import com.managetruck.entidades.Transportista;
import com.managetruck.entidades.Usuario;
import com.managetruck.errores.ErroresServicio;
import com.managetruck.repositorios.RepositorioTransportista;
import com.managetruck.servicios.CamionServicio;
import com.managetruck.servicios.TransportistaServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/transportista")
public class TransportistaController {

    @Autowired
    CamionServicio camionServicio;

    @Autowired
    TransportistaServicio transportistaServicio;

    @Autowired
    RepositorioTransportista repositorioTransportista;

    @PostMapping("/registra")
    public String registroProveedor(ModelMap model, String nombre, String apellido, String mail, String password, MultipartFile foto, String zona, String telefono, Integer pesoMaximo, String descripcion, @RequestParam String modelo, Integer anio, String patente, Integer poliza, List<MultipartFile> fotos) throws ErroresServicio {
        try {
            camionServicio.crearCamion(pesoMaximo, modelo, descripcion, anio, patente, poliza, fotos);
            transportistaServicio.crearTransportista(nombre, apellido, mail, password, foto, zona, telefono);
        } catch (ErroresServicio es) {
            model.put("error", es.getMessage());
            model.put("nombre", nombre);
            model.put("apellido", apellido);
            model.put("mail", mail);
            model.put("password", password);
            model.put("foto", foto);
            model.put("zona", zona);
            model.put("telefono", telefono);
            model.put("pesoMaximo", pesoMaximo);
            model.put("modelo", modelo);
            model.put("descripcion", descripcion);
            model.put("anio", anio);
            model.put("patente", patente);
            model.put("poliza", poliza);
            model.put("fotos", fotos);

            return "index";//modificar nombre de vista, no debe redirigir a index si no a la vista que utilizaremos
        }
        return "redirect:/transportista_form";
    }

    @GetMapping("/registro")
    public String mostrarPaginaRegistro() {
        return "transportista_form";
    }

    @GetMapping("")
    public String listarTransportista(ModelMap modelo, @RequestParam(required = false) String error, @RequestParam(required = false) String nombre) {
        if (nombre != null) {
            List<Transportista> transportistas = repositorioTransportista.buscarTransportistaPorNombre2(nombre);
            modelo.addAttribute("tittle", "Listado Transportistas");
            modelo.addAttribute("transportistas", transportistas);
        } else {
            List<Transportista> transportistas = repositorioTransportista.findAll();
            modelo.addAttribute("tittle", "Listado Transportistas");
            modelo.addAttribute("transportistas", transportistas);
        }

        return "index";//modificar nombre de vista, no debe redirigir a index si no a la vista que utilizaremos
    }

    @GetMapping("/bajar")
    public String darDeBaja(ModelMap model, @RequestParam(required = true) String id) throws ErroresServicio {
        try {
            transportistaServicio.deshabilitarTransportista(id);
        } catch (ErroresServicio ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/index";//modificar nombre de vista, no debe redirigir a index si no a la vista que utilizaremos 
    }

    @GetMapping("/alta")
    public String darDeAlta(ModelMap model, @RequestParam(required = true) String id) throws ErroresServicio {
        try {
            transportistaServicio.habilitarTransportista(id);
        } catch (ErroresServicio ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "redirect:/index";//modificar nombre de vista, no debe redirigir a index si no a la vista que utilizaremos 
    }

    @PostMapping("/modificar")
    public String modificar(HttpSession session, ModelMap modelo, String id, String nombre, String apellido, String mail, String password, MultipartFile foto, String zona, String telefono, Camion camion, double valoracion, Integer cantidadViajes) throws ErroresServicio {
        //verificacion de que el usuario que esta modificando sea el mismo que va a modificar
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || login.getId().equals(id)) {
            return "redirect:/login";
        }
        try {
            transportistaServicio.modificarUsuario(id, nombre, apellido, mail, password, foto, zona, telefono, camion, valoracion, cantidadViajes);
        } catch (ErroresServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("id", id);
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("mail", mail);
            modelo.put("password", password);
            modelo.put("foto", foto);
            modelo.put("zona", zona);
            modelo.put("telefono", telefono);
            modelo.put("camion", camion);
            modelo.put("valoracion", valoracion);
            modelo.put("cantidadViajes", cantidadViajes);

        }
        return "index";//modificar nombre de vista, no debe redirigir a index si no a la vista que utilizaremos 
    }

    @GetMapping("/indexTransportista")
    public String indexTransportista() {
        return "indexTransportista";
    }

}
