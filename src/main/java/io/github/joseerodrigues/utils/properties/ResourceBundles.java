package io.github.joseerodrigues.utils.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import io.github.joseerodrigues.utils.Checks;

/**
 * Obtém ResourceBundles que permitam interpolação de propriedades (props a referenciar props)
 *
 * @see InterpolationResourceBundle
 * @author 92429
 */
public final class ResourceBundles {

	private ResourceBundles(){
		throw new IllegalAccessError();
	}

	public static ResourceBundle fromFile(String baseName){
		Checks.checkNullOrEmpty(baseName, "baseName");
		return new InterpolationResourceBundle(ResourceBundle.getBundle(baseName));
	}

	/**
	 * Agrega ResourceBundles, e permite interpolação de propriedades entre eles.
	 *
	 * Ex:
	 *
	 * ResourceBundle sgdBundle = ResourceBundles.fromFile("sgd");
	 * ResourceBundle tabelasBundle = ResourceBundles.fromDB(conn, "TABELAS");
	 *
	 * ResourceBundle bundle = ResourceBundles.fromMultiple(sgdBundle, tabelasBundle);
	 *
	 *
	 * Ao obter o valor de uma propriedade, a ordem de pesquisa nos ResourceBundles é a especificada
	 * no argumento. No exemplo acima, primeiro seria tentado o sgdBundle, e só depois o tabelasBundle.
	 *
	 * Se a propriedade não for encontrada em nenhum deles, então mantém-se o contracto do ResourceBundle e
	 * � emitida uma MissingResourceException.
	 *
	 *
	 * @param bundles Lista de bundles a pesquisar, por ordem.
	 * @return Um ResourceBundle que obtem os dados de vários ResourceBundles
	 * @author 92429
	 */
	public static ResourceBundle fromMultiple(ResourceBundle ... bundles){
		Checks.checkNull(bundles, "bundles");

		List<ResourceBundle> filteredBundles = filterNotNull(Arrays.asList(bundles));

		return new InterpolationResourceBundle(new CompoundResourceBundle(filteredBundles));
	}

	private static List<ResourceBundle> filterNotNull(List<ResourceBundle> list) {
		ArrayList<ResourceBundle> ret = new ArrayList<>(list.size());

		for (ResourceBundle rb : list) {
			if (rb != null) {
				ret.add(rb);
			}
		}

		return ret;
	}
}
