package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * 
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent{

	//matrice des valeurs Q
	protected HashMap<Etat, HashMap<Action, Double>> qValues;

	/**
	 * 
	 * @param alpha
	 * @param gamma
	 * @param Environnement
	 */
	public QLearningAgent(double alpha, double gamma,
			Environnement _env) {
		super(alpha, gamma,_env);
		qValues = new HashMap<>();

	}




	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  
	 *  renvoi liste vide si aucunes actions possibles dans l'etat 
	 */
	@Override
	public List<Action> getPolitique(Etat e) {

		List<Action> actions = new ArrayList<Action>();

		Double valueMax = this.getValeur(e);
		if(qValues.get(e) != null)
			for (Map.Entry<Action, Double> entry : qValues.get(e).entrySet()) {
				if (valueMax.equals(entry.getValue()))
					actions.add(entry.getKey());
			}

		return actions;

	}

	/**
	 * @return la valeur d'un etat
	 */
	@Override
	public double getValeur(Etat e) {

		Double res = 0.;
		if(qValues.get(e) != null)
			for(Double value : qValues.get(e).values())
				if (res < value)
					res = value;
		return res;
	}

	/**
	 * 
	 * @param e
	 * @param a
	 * @return Q valeur du couple (e,a)
	 */
	@Override
	public double getQValeur(Etat e, Action a) {

		if(qValues.get(e) != null && qValues.get(e).get(a) != null){
			return qValues.get(e).get(a);
		}
		setQValeur(e, a, 0.0);
		return qValues.get(e).get(a);
	}

	/**
	 * setter sur Q-valeur
	 */
	@Override
	public void setQValeur(Etat e, Action a, double d) {


		if(!qValues.containsKey(e)) {
			HashMap<Action, Double> couple = new HashMap<>();
			couple.put(a,d);
			qValues.put(e, couple);
		}
		else {
			qValues.get(e).put(a, d);
		}

		//Mise a jour vmin et vmax pour affichage gradient de couleur
		vmax = Math.max(d, vmax);
		vmin = Math.min(d, vmin);

		this.notifyObs();
	}


	/**
	 *
	 * mise a jour de la Q-valeur du couple (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		Double valeur = (1-this.alpha)*this.getQValeur(e,a)+this.alpha*(reward+this.gamma*this.getValeur(esuivant));
		this.setQValeur(e, a, valeur);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	/**
	 * reinitialise les Q valeurs
	 */
	@Override
	public void reset() {
		super.reset();
		this.episodeNb = 0;
		this.vmax = Double.MIN_VALUE;
		this.vmin = Double.MAX_VALUE;
		this.qValues = new HashMap<Etat, HashMap<Action, Double>>();

		this.notifyObs();
	}

}
