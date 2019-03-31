package ch.heigvd.res.pagedidier.Calculator;

/**
 * Objet décrivant une opération modulo n sur des entiers
 * @author Nohan Budry, Didier Page
 */
public interface Operation {

    /**
     * Effectue une opération en modulo n
     * @param a Première opérande
     * @param b Seconde opérande
     * @return Résultat du calcul (entre 0 et modulo - 1)
     */
    int calculate(int a, int b);
}
