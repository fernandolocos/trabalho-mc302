package pt.c03ensaios.fejao.teste;

import pt.c01interfaces.s01chaveid.s01base.impl.BaseConhecimento;
import pt.c01interfaces.s01chaveid.s01base.inter.IEnquirer;
import pt.c01interfaces.s01chaveid.s01base.inter.IResponder;
import pt.c03ensaios.fejao.teste.EnquirerAdvancedTeste;
import pt.c03ensaios.frango.appTest.Responder;

public class AppTeste01 {


	public static void main(String[] args) {
		String nomeAnimal;

        BaseConhecimento bc = new BaseConhecimento();
        String listaNomes[] = bc.listaNomes();

        for (int i = 0; i < listaNomes.length; i++) {
            nomeAnimal = listaNomes[i];
            System.out.print("Advanced: ");
            enquirerAdvancedTest(nomeAnimal);
        }
		
	}

    public static void enquirerAdvancedTest(String nomeAnimal) {
        IEnquirer ea = new EnquirerAdvancedTeste();
        IResponder ra = new Responder(nomeAnimal);

        ea.connect(ra);
    }

}
