/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.managetruck.servicios;

import com.managetruck.entidades.Comprobante;
import com.managetruck.entidades.Proveedor;
import com.managetruck.entidades.Viaje;
import com.managetruck.errores.ErroresServicio;
import com.managetruck.repositorios.RepositorioComprobante;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComprobanteServicio {

    @Autowired(required = true)
    RepositorioComprobante repositorioComprobante;

    @Transactional //cambio el metodo porque cuando se crea un comprobante no se puede valorar porque no esta hecho el viaje
    public void crearComprobante(Proveedor proveedor, Viaje viaje) throws ErroresServicio {
        validarComprobante(proveedor, viaje);
        Comprobante comprobante = new Comprobante();
        comprobante.setProveedor(proveedor);

        comprobante.setViaje(viaje);
        repositorioComprobante.save(comprobante);
    }

    @Transactional
    private void ModificarComprobante(String id, Integer valoracion, Proveedor proveedor, Viaje viaje) throws ErroresServicio {
        Optional<Comprobante> respuesta = repositorioComprobante.findById(id);
        validarComprobante(proveedor, viaje);

        if (respuesta.isPresent()) {
            Comprobante comprobante = respuesta.get();
            if (comprobante.getViaje().isAlta() == false) {
                //para cincorporar la valoracion al finalizar el viaje
                validarvaloracion(valoracion);
                comprobante.setValoracion(valoracion);
                repositorioComprobante.save(comprobante);
            } else {
                //para modificar el comprobante completo
                comprobante.setProveedor(proveedor);
                validarvaloracion(valoracion);
                comprobante.setValoracion(valoracion);
                comprobante.setViaje(viaje);
                repositorioComprobante.save(comprobante);
            }
        }
    }

    private void validarComprobante(Proveedor proveedor, Viaje viaje) throws ErroresServicio {
        if (proveedor == null) {
            throw new ErroresServicio("Debe ingresar un proveedor");
        }
        if (viaje == null) {
            throw new ErroresServicio("Debe ingresar un viaje");
        }
    }
    private void validarvaloracion(Integer valoracion) throws ErroresServicio {
        if (valoracion == null) {
            throw new ErroresServicio("Debe ingresar una valoracion");
        }
    }
    /*private void Valoracion(){
}*/

}
