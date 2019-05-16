package dominio;

import dominio.repositorio.RepositorioProducto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	Logger log = Logger.getLogger(Vendedor.class.getName());

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
	public static final String EL_PRODUCTO_NO_CUENTA_GARANTIA = "Este producto no cuenta con garantía extendida";
	public static final double PRECIO=500000;
	public static final double PRECIOMAYOR = 0.2;
	public static final double PRECIOMENOR = 0.1;

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioProducto = repositorioProducto;
		this.repositorioGarantia = repositorioGarantia;

	}

	public void generarGarantia(String codigo,String nombreCliente) {
		log.info("Codigo ingresado:    " + codigo);
		int cantVocales = validarVocales(codigo);
		log.info("Cantidad de vocales: " + cantVocales);
		boolean tieneG = tieneGarantia(codigo);
		log.info("Tiene garantia ? " + tieneG);
		if (tieneG) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
		} else if(cantVocales>=3) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_NO_CUENTA_GARANTIA);
		}else{
			Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
			log.info("precio del producto " + producto.getPrecio());
			Date fechaCalculada = calcularFecha(producto.getPrecio());
			double precioGarantia = 0;
			if(producto.getPrecio() >PRECIO) {
				precioGarantia = producto.getPrecio()*PRECIOMAYOR;
			}else{
				precioGarantia = producto.getPrecio()*PRECIOMENOR;
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			log.info("Valor de la garantia " + precioGarantia);
			log.info("Fecha calculada " + df.format(fechaCalculada));
			GarantiaExtendida gExtendida = new GarantiaExtendida(producto, new Date(), fechaCalculada, precioGarantia, nombreCliente);
			repositorioGarantia.agregar(gExtendida);
			
		}



	}

	public boolean tieneGarantia(String codigo) {

		try {
			Producto prod = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
			
			return prod != null ? true : false;

		} catch (Exception e) {

			log.log(Level.SEVERE, "Error -> ", e);
		}
		return false;
	}

	public int validarVocales(String codigo) {
		int vocales = 0;
		
		Pattern patternVow = Pattern.compile("[euioa]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcherVow = patternVow.matcher(codigo.trim());
		while (matcherVow.find()) {
			vocales++;
		}
		return vocales;
	}
	
	public Date calcularFecha(double precio) {
		
		Calendar fechaActual = Calendar.getInstance();
		if(precio > PRECIO) {
			int days=0;
						
			for (int i = 0; i < 200; i++) {
				
				fechaActual.add(Calendar.DATE, 1);
				
				if(fechaActual.get(Calendar.DAY_OF_WEEK)==2) {
					days++;
				}
			}
			fechaActual.add(Calendar.DATE, days);
			if(fechaActual.get(Calendar.DAY_OF_WEEK)==1) {
				fechaActual.add(Calendar.DATE, 2);
			}
			
			
		}else {
			fechaActual.add(Calendar.DATE, 100);
		}
		return fechaActual.getTime();
	}

}
