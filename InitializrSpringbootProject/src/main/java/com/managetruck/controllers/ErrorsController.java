    package com.managetruck.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorsController implements ErrorController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public String paginaError(Model model, HttpServletRequest httpServletRequest) {
        String errormensaje = "";
        int codigoError = (int) httpServletRequest.getAttribute("javax.servlet.error.status_code");
        String error = null;
        switch (codigoError) {
            
            case 400:
                
                errormensaje = "El recurso solicitado no existe";
                break;
            case 401:
                errormensaje = "Usted no se encuentra autorizado, tiene que iniciar sesión";
                break;
            case 403:
                error="403";
                errormensaje = "Usted no tiene los permisos para acceder a los recursos, no tiene el rol necesario";
                break;
            case 404:
                error="404";
                errormensaje = "El recurso solicitado no se ha encontrado";
                break;
            case 500:
                error="500";//que carajo es esto
                errormensaje = "El servidor no pudo realizar la petición con éxito";
                break;
            case 408:
                error="408"; 
                break;
            
            default:
        }
        model.addAttribute("codigo", codigoError);
        model.addAttribute("mensaje", errormensaje);
        return "Errores.html";
    }

}
