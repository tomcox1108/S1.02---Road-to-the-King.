/*etapes :
-ecran titre
-menu (jouer/parametres/comptes)
-jouer -> choisir le nb de joueurs/changer les parametres
-jouer -> bon para -> se connecter ou jouer comme invité (se connecter demande un mdp)
-jouer -> bon para -> connectés -> lancer la partie. 10*5 cases, chaque bonne réponse on avance d'une case. Pleins d'évenements random peuvent arriver a chaque case (malus,bonus,pouvoir) avec une question a la fin*/
import extensions.File;
class RoadToTheKing extends Program{
//-------------program-----------------------------------------------------------------------------
    void algorithm(){
        nbJoueurs;
        tempsQuestions;
        ecranTitre();
        int premierChoix = premierChoix();
        if(premierChoix == 1){
            nbJoueurs = nbJoueurs();
        }
        
    }
//-------------ecran titre-----------------------------------------------------------------------------
    void ecranTitre(){
        File ecranTitre = newFile("ecranTitre.txt");
        println("--------------------------------------------------------------");
        println("Merci de bien vouloir mettre le jeu en plein ecran pour jouer.");
        println("--------------------------------------------------------------");
        print("Pressez ENTRÉE pour continuer.");
        readString();
        while(ready(ecranTitre)){
            println(readLine(ecranTitre));
        }
        readString();
    }
//-------------premier choix-----------------------------------------------------------------------------

    int premierChoix(){
        int result;
        println("1 : Jouer");
        println("2 : Vos comptes");
        println("3 : Règles");
        String saisie;
        do{
            print("Choisissez ce que vous voulez faire : ");
            saisie = readString();
            if(controleSaisiePremierChoix(saisie)){
                println("WIP");
            }else{
                println("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(controleSaisiePremierChoix(saisie)));

        if(charAt(saisie,1) == '1'){
            nbJoueurs()
            result = 1;
        }
        if(charAt(saisie,1) == '2'){
            result = 2;
        }
        if(charAt(saisie,1) == '3'){
            result = 2;
        }
        return result;
    }
    boolean controleSaisiePremierChoix(String saisie){
        boolean result = true;
        if(length(saisie) > 1 ||
            charAt(saisie, 0) > '3' ||
            charAt(saisie, 0) < '1'){
            result = false;
        }
        return result;
    }
//-------------regles-----------------------------------------------------------------------------

//-------------se connecter-----------------------------------------------------------------------------

//-------------jouer-----------------------------------------------------------------------------

//-------------tests-----------------------------------------------------------------------------
    void test_controleSaisiePremierChoix(){
        assertTrue(controleSaisiePremierChoix("2"));
        assertFalse(controleSaisiePremierChoix("4"));
        assertFalse(controleSaisiePremierChoix("-1"));
        assertFalse(controleSaisiePremierChoix("0"));
        assertFalse(controleSaisiePremierChoix("abcd"));

    }
}