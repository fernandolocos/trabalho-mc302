package pt.c03ensaios.fejao.teste;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.c01interfaces.s01chaveid.s01base.impl.BaseConhecimento;
import pt.c01interfaces.s01chaveid.s01base.inter.IBaseConhecimento;
import pt.c01interfaces.s01chaveid.s01base.inter.IDeclaracao;
import pt.c01interfaces.s01chaveid.s01base.inter.IEnquirer;
import pt.c01interfaces.s01chaveid.s01base.inter.IObjetoConhecimento;
import pt.c01interfaces.s01chaveid.s01base.inter.IResponder;
import pt.c03ensaios.fejao.PossibleAnimalsHash;
import pt.c03ensaios.frango.IQuestionsHash;
import pt.c03ensaios.frango.QuestionsHash;
import pt.c03ensaios.frango.appTest.Responder;
import anima.factory.IGlobalFactory;
import anima.factory.context.componentContext.ComponentContextFactory;

public class EnquirerAdvancedTeste implements IEnquirer{

	private IBaseConhecimento baseConhecimento;

	// essas variaveis s�o estaticas para poder economizar no tempo de execu��o
	// quando presivar instanciar varios enquirers
	private static String[] listaNomes;
	private static IQuestionsHash hashRespostasSim;
	private static IQuestionsHash hashRespostasNao;
	private static IQuestionsHash hashRespostasNaoSei;
	private static List<String> listaPerguntas = new ArrayList<String>();
	private static HashMap<String, IResponder> responders = new HashMap<String, IResponder>();
	private static HashMap<String, IObjetoConhecimento> objs = new HashMap<String, IObjetoConhecimento>();
	private static Boolean jaCacheado = false;

	public EnquirerAdvancedTeste() {
		baseConhecimento = new BaseConhecimento();
		listaNomes = baseConhecimento.listaNomes();

		// so coloca os valores nas variaveis na primeira execu��o

		if (jaCacheado == false) {

			// aqui � contruido um hash com lista ligadas que quardam os animais
			// indexados pelas perguntas, sendo que existe um hash para cada
			// resposta

			try {
				// creates a global factory
				IGlobalFactory factory = ComponentContextFactory
						.createGlobalFactory();

				factory.registerPrototype(QuestionsHash.class);

				// instances the component based on its URI
				hashRespostasSim = factory
						.createInstance("<http://purl.org/dcc/pt.c03ensaios.frango.QuestionsHash>");

				hashRespostasNao = factory
						.createInstance("<http://purl.org/dcc/pt.c03ensaios.frango.QuestionsHash>");

				hashRespostasNaoSei = factory
						.createInstance("<http://purl.org/dcc/pt.c03ensaios.frango.QuestionsHash>");
			} catch (Exception e) {
				e.printStackTrace();
			}

			// guarda uma lista de todas as perguntas
			listaPerguntas = new ArrayList<String>();

			for (int i = 0; (i < listaNomes.length); i++) {
				IObjetoConhecimento obj;
				obj = baseConhecimento.recuperaObjeto(listaNomes[i]);
				objs.put(listaNomes[i], obj);

				IDeclaracao decl = obj.primeira();

				while (decl != null) {
					if (!isQuestion(decl.getPropriedade())) {
						insertIntoHash(decl.getPropriedade());
						addQuestion(decl.getPropriedade());
					}
					decl = obj.proxima();
				}
			}
		}

		jaCacheado = true;
	}

	public void connect(IResponder responder) {
		boolean acertei, encontrado = false;
		String nomeAnimal = null;
		//List<String> listaPossiveisAnimais = new ArrayList<String>();
		PossibleAnimalsHash hashAnimais = new PossibleAnimalsHash();

		// utiliza a lista de perguntas que foi montada anteriormente e vai
		// perguntando at� os animais possiveis para a resposta seja somente 1
		for (int i = 0; ((i < listaPerguntas.size()) && (!encontrado)); i++) {
			String resposta = responder.ask((String) listaPerguntas.get(i));

			// determina uma nova lista de possiveis animais
			hashAnimais.DeterminesPossibleAnimals(listaPerguntas.get(i), resposta);
			
			// insere a lista de possiveis animais
			//hashAnimais.putPossibleAnimalsList(listaPossiveisAnimais);

			if (hashAnimais.getListSize() == 1) {
				nomeAnimal = hashAnimais.getPossibleAnimalsList().get(0);
				encontrado = true;
			}
		}

		if (nomeAnimal != null) {
			acertei = responder.finalAnswer(nomeAnimal);
		} else {
			acertei = responder.finalAnswer("nao sei");
		}

		if (acertei) {
			System.out.println("Oba! Acertei! - " + nomeAnimal);
		} else {
			System.out.println("fuem! fuem! fuem!");
		}
	}

	private void insertIntoHash(String question) {
		for (int i = 0; i < listaNomes.length; i++) {
			IResponder responder = responders.get(listaNomes[i]);
			if (responder == null) {
				responder = new Responder(listaNomes[i]);
				responders.put(listaNomes[i], responder);
			}

			String resposta = responder.ask(question);

			if (resposta.equalsIgnoreCase("nao")) {
				hashRespostasSim.putAnimal(question, listaNomes[i]);
			} else if (resposta.equalsIgnoreCase("sim")) {
				hashRespostasNao.putAnimal(question, listaNomes[i]);
			} else if (resposta.equalsIgnoreCase("nao sei")) {
				hashRespostasNaoSei.putAnimal(question, listaNomes[i]);
			}
		}
	}

	private void addQuestion(String question) {
		listaPerguntas.add(question);
	}

	private boolean isQuestion(String question) {
		return (listaPerguntas.contains(question));
	}
}