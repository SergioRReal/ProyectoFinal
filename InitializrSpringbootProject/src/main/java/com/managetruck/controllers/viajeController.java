package com.managetruck.controllers;

import com.managetruck.entidades.Comprobante;
import com.managetruck.entidades.Usuario;
import com.managetruck.errores.ErroresServicio;
import com.managetruck.servicios.ComprobanteServicio;
import com.managetruck.servicios.TransportistaServicio;
import com.managetruck.servicios.ViajeServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/viaje")
public class viajeController {

    @Autowired
    private ViajeServicio viajeServicio;
    @Autowired
    private TransportistaServicio transportistaServicio;
    @Autowired
    private ComprobanteServicio comprobanteServicio;

    @GetMapping("/pedido")
    public String inicioViaje() {
        return null;
    }

    @PostMapping("/pedido")
    public String comienzoViaje(String idProveedor, @RequestParam Integer peso, @RequestParam Integer kmRecorridos, @RequestParam String tipoCargas, @RequestParam String destino, @RequestParam String origen) {
        try {
            viajeServicio.crearViaje(idProveedor, peso, kmRecorridos, tipoCargas, destino, origen);
        } catch (ErroresServicio ex) {
            Logger.getLogger(viajeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @GetMapping("/modificar-viaje")
    public String modificarViaje() {
        return null;
    }

    @PostMapping("/modificar-viaje")
    public String cambiarViaje(HttpSession session, @RequestParam String id, @RequestParam Integer peso, @RequestParam Integer kmRecorridos, @RequestParam String tipoCargas, @RequestParam String destino, @RequestParam String origen) {

        try {
            //lohago para verificar que el provedor y el viaje estan en el mismo comprobante y poder usar 
            //el comprobante para verificar que el usuario que se conecto es el mismo
            Comprobante comprobante = comprobanteServicio.buscarComprobanteIdViaje(id);
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || login.getId().equals(comprobante.getProveedor().getId())) {
                return "redirect:/login";
            }
            viajeServicio.ModificarViaje(id, peso, kmRecorridos, tipoCargas, destino, origen);
        } catch (ErroresServicio ex) {
            Logger.getLogger(viajeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @PostMapping("/finalizar")
    public String finalizarViaje(HttpSession session,@RequestParam String id, @RequestParam Integer valoracion) {
        try {
            Comprobante comprobante = comprobanteServicio.buscarComprobanteIdViaje(id);
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            if (login == null || login.getId().equals(comprobante.getProveedor().getId())) {
                return "redirect:/login";
            }
            viajeServicio.BajaViaje(id);
            comprobanteServicio.ValorarTrasnportista(comprobante.getID(), valoracion);
        } catch (ErroresServicio ex) {
            Logger.getLogger(viajeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/valorar";
    }
    @GetMapping("/valorar")
    public String puntear (String id_comprobante){
        return null;
    }
    @PostMapping("/validar")
    public String finalizarPuntuacion(String id_comprobante, Integer valoracion){
        
        Comprobante comprobante;
        try {
            comprobante = comprobanteServicio.buscarComprobanteId(id_comprobante);
            comprobanteServicio.ValorarTrasnportista(comprobante.getID(), valoracion);
        } catch (ErroresServicio ex) {
            Logger.getLogger(viajeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
