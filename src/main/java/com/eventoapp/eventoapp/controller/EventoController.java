package com.eventoapp.eventoapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.eventoapp.models.Convidado;
import com.eventoapp.eventoapp.models.Evento;
import com.eventoapp.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private ConvidadoRepository convidadoRepository;

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {
		return "evento/formEvento";
	}

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String form(Evento evento) {

		// salva o evento no banco de dados
		eventoRepository.save(evento);

		return "redirect:/evento";

	}

	@RequestMapping(value = "evento")
	public ModelAndView listaEvento() {
		// passa a página que o modelAndView vai renderizar
		ModelAndView modelAndView = new ModelAndView("index");
		Iterable<Evento> evento = eventoRepository.findAll();
		modelAndView.addObject("evento", evento);
		return modelAndView;
	}

	@RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		// busca evento
		Evento evento = eventoRepository.findByCodigo(codigo);
		// passa a página que o modelAndView vai renderizar
		ModelAndView modelAndView = new ModelAndView("evento/detalhesEvento");
		modelAndView.addObject("evento", evento);
		// busca convidados do evento
		Iterable<Convidado> convidado = convidadoRepository
				.findByEvento(evento);
		modelAndView.addObject("convidado", convidado);
		return modelAndView;

	}

	@RequestMapping(value = "/deletar")
	public String deletarEvento(long codigo) {
		Evento evento = eventoRepository.findByCodigo(codigo);
		eventoRepository.delete(evento);
		return "redirect:/evento";
	}

	@RequestMapping(value = "/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado = convidadoRepository.findByRg(rg);
		convidadoRepository.delete(convidado);

		Evento evento = convidado.getEvento();
		long codigoLong = evento.getCodigo();
		String codigo = "" + codigoLong;
		return "redirect:/" + codigo;
	}

	@RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
	public String detalhesEventosPost(@PathVariable("codigo") long codigo,
			@Valid Convidado convidado, BindingResult result,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/{codigo}";
		} else {
			Evento evento = eventoRepository.findByCodigo(codigo);
			convidado.setEvento(evento);
			convidadoRepository.save(convidado);
			attributes.addFlashAttribute("mensagem",
					"Convidado adicionado com sucesso!");
			return "redirect:/{codigo}";
		}
	}

}
