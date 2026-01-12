/* 
TODO : 
- rédiger et implémenter les règles
- implementer la fonction de save a la fin de chaque game
- implémenter la fonction d'objets (simple) -> objets en eux meme fait, commandes externes aussi (sauf usage et aide)
- implémenter les lectures et usages d'objet (1 fonction / objet)
- //implémenter les boss ?
*/

import extensions.File;
import extensions.CSVFile;
class RoadToTheKing extends Program{
//variables globales

    //utilitaires
    final String clear = "\033[H\033[2J\033[3J";
    final String ensembleSkins = "◎⩇⁝⬔🗝⧖⚙𝓩𝓐⚖ڽ﷼⚔✟✠☠⚡︎⌘⏀⛩☯✧❂*☭";

    //csv
    CSVFile comptes = loadCSV("CSV/comptes.CSV");
    final CSVFile dilemme = loadCSV("CSV/dilemmes.CSV",'_');
    final CSVFile evenements = loadCSV("CSV/evenements.CSV",'_');
    final CSVFile items = loadCSV("CSV/items.CSV",'_');
    final CSVFile questions = loadCSV("CSV/questions.CSV",'_');
    final CSVFile zones = loadCSV("CSV/zones.CSV",'_');

    //structure du jeu
    Joueur[] ensembleJoueur; //trouver joueur actu et load les données dans les bonnes cases
    ContenuCases[][] contenuChaqueCase; 
    Partie partie;
    int joueurActu = 0; //joueur actuel
    int rejouer = 3;

    //cases
    final String caseVide = "   ";
    final String caseMonolith = "🗿 ";
    String caseJoueur; //impossible a créer au début (NullPointerException error)
    String caseJoueur2;
    String caseJoueur3;
    String caseJoueur4;
                                                    
//-------------programme-----------------------------------------------------------------------------
    void algorithm(){
        boolean quitter = false;
        ecranTitre();
        do{ //boucle pour revenir au premier choix (rejouer = 4)
            int premierChoix = premierChoix();
            if(rejouer == 4){
                rejouer = 3;
                print(clear);
            }

            //LE JEU
            if(premierChoix == 1){
                int tempsQuestions = -1;
                int nbJoueurs = -1;
                int longueurPartie = -1;

                while(rejouer == 3 || rejouer == 2){ //rejouer normal/mi rapide
                    parametres(tempsQuestions, nbJoueurs, longueurPartie);
                    //le jeu ihi (trop bien)
                    leJeu();
                }

                while(rejouer == 1){ //rejouer rapide
                    rejouerRapide();
                    leJeu();
                }

                if(rejouer == 5){ //quitter...
                    println(clear + "merci beaucoup d'avoir joué ! :)");
                    quitter = true;
                }
            }

            //LES COMPTES
            if (premierChoix == 2){
                listeCompte();
            }

            //LES REGLES
            /* if(premierChoix == 3){
                File règles = newFile("txt/règles.txt");
                while(ready(règles)){
                    println(readLine(règles));
                }*
            } */

            //QUITTER LE PROGRAMME
            if (premierChoix == 4){
                quitter = true;
            }
        }while(quitter == false); 
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////____  /////////////////////////////////////////////////////////////////
//////////////////////////////|GRUB| /////////////////////////////////////////////////////////////////
//////////////////////////////|____| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------ecran titre-----------------------------------------------------------------------------
    void ecranTitre(){
        print(clear);
        File ecranTitre = newFile("txt/ecranTitre.txt");
        ligne();
        println("Merci de bien vouloir mettre le jeu en plein ecran pour jouer.");
        ligne();
        print("Pressez ENTRÉE pour continuer.");
        readString();
        while(ready(ecranTitre)){
            println(readLine(ecranTitre));
        }
        readString();
        print(clear);
    }
//-------------premier choix-----------------------------------------------------------------------------

    int premierChoix(){
        ligne();
        println("1 : Jouer");
        println("2 : Vos comptes");
        println("3 : Règles");
        println("4 : Quitter");
        ligne();
        String saisie;
        do{
            print("Choisissez ce que vous voulez faire : ");
            saisie = readString();
            if(!(controleSaisieInt(saisie,"4"))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                ligne();
                saisie = "9";  
            }
        }while(!(controleSaisieInt(saisie, "4")));

        return StringToInt(saisie);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////__________  /////////////////////////////////////////////////////////////////
//////////////////////////////|GÉNÉRALES|  /////////////////////////////////////////////////////////////////
//////////////////////////////|_________|  /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------générales-----------------------------------------------------------------------------
    boolean controleSaisieInt(String saisie, String max){
        boolean result = true;
        if(equals(saisie,"")){
            result = false;
        }
        else if(StringToInt(saisie) > StringToInt(max) || StringToInt(saisie) < 1 || equals(saisie,"")){
            result = false;
        }else{
            for(int i = 0; i < length(saisie); i++){
                if(charAt(saisie,i) > '9' || charAt(saisie,i) < '0'){
                    result = false;
                }
            }
        }
        return result;
    }

    void ligne(){
        println("--------------------------------------------------------------");
    }

    void ligne(int n, char c){
        for(int i = 0;i<n;i++){
            print(c);
        } 
         print("");
    }

    int rAssociéeAuJoueur(int joueur){
        if(joueur == 1 || joueur == 4){
            return 255;
        }else{
            return 0;
        }
    }
    int vAssociéeAuJoueur(int joueur){
        if(joueur == 3 || joueur == 4){
            return 255;
        }else{
            return 0;
        }
    }
    int bAssociéeAuJoueur(int joueur){
        if(joueur == 2 || joueur == 4){
            return 255;
        }else{
            return 0;
        }
    }

    int posX(int joueur){
        if(joueur == 1 && length(ensembleJoueur) == 3 || 
            joueur == 1 && length(ensembleJoueur) == 4){
            return 0;
        }
        else if(joueur == 1 && length(ensembleJoueur) == 2 ||
            joueur == 2 && length(ensembleJoueur) == 4){
            return 1;
        }
        else if(joueur == 1 && length(ensembleJoueur) == 1 || 
            joueur == 2 && length(ensembleJoueur) == 3 || 
            joueur == 3 && length(ensembleJoueur) == 4){
            return 2;
        }
        else if(joueur == 2 && length(ensembleJoueur) == 2 ||
            joueur == 4 && length(ensembleJoueur) == 4){
            return 3;
        }else{
            return 4;
        }
    }

    void box(String texte) {
        print('╔');
        ligne(48,'═');
        println('╗');
        int debut = 0;
        while (debut < length(texte)) {
            int fin = debut + 46;
            if(fin > length(texte)) {
                fin = length(texte);
            }else{
            int i = fin - 1;
                while (i > debut && charAt(texte, i) != ' ') {
                    i = i - 1;
                }
                if (i > debut) {
                fin = i;
                }
            }
            print("║ ");
            print(substring(texte, debut, fin));
            int nbEspaces = 46 - (fin - debut);
            int j = 0;
            while (j < nbEspaces) {
                print(" ");
                j = j + 1;
            }
            println(" ║");
            debut = fin + 1;
        }
        print('╚');
        ligne(48,'═');
        println('╝');
    }
//-------------convertions-----------------------------------------------------------------------------
    int StringToInt(String entree){
        int max = 1;
        for(int i = 1; i< length(entree);i++){
            max = max * 10;
        }
        int result = 0;
        int j = 0;
        for(int i = max ; i>=1; i = i / 10){
            result = result + charToInt(charAt(entree,j)) * i;
            j++;
        }
        return result;
    }

    char intToChar(int entree){
        return (char)(entree + '0');
    }

    int charToInt(char entree){
        return entree - '0';
    }

    String intToString(int entree){
        return "" + entree;
    }

    String convertionEnMinute(int secondes){
        String result = "";
        int minutes = 0;
        if(secondes < 60){
            return "" + secondes;
        }else{
            for(int i = 0; i < secondes/60; i++){
                minutes ++;
            }
            return minutes + " minutes et " + secondes%60;
        }
    }
    
//-------------creation types-----------------------------------------------------------------------------
    Joueur newJoueur(int jno, char skin, String nom, int r, int v, int b, int posX){
        Joueur j = new Joueur();
        j.jno = jno;
        j.skin = skin;
        j.nom = nom;
        j.r = r;
        j.v = v;
        j.b = b;
        j.posX = posX;
        return j;
    }

    Partie newPartie(int coeff, int nbJoueurs, int score){
        Partie p = new Partie();
        p.coeffTemps = coeff;
        p.nbJoueurs = nbJoueurs;
        p.scoreDeVictoire = score;
        return p;
    }

    ContenuCases newContenuCases(String event, String question1, String question2, String question3, String apparitionItem, String descItem, int ligneItem, int reponse1, int reponse2, int reponse3, int temps){
        ContenuCases c = new ContenuCases();
        c.evenement = event;
        c.question1 = question1;
        c.question2 = question2;
        c.question3 = question3;
        c.apparitionItem = apparitionItem;
        c.descItem = descItem;
        c.ligneItem = ligneItem;
        c.reponse1 = reponse1;
        c.reponse2 = reponse2;
        c.reponse3 = reponse3;
        c.temps = temps;
        return c;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////_____  /////////////////////////////////////////////////////////////////
//////////////////////////////|JOUER| /////////////////////////////////////////////////////////////////
//////////////////////////////|_____| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------parametres-----------------------------------------------------------------------------
    void parametres(int tempsQuestions, int nbJoueurs, int longueurPartie){
        //phase de choix des joueurs
        nbJoueurs = nbJoueurs();
        println("Cette partie aura " + nbJoueurs + " joueurs.");
        ensembleJoueur = new Joueur[nbJoueurs];
        ensembleJoueur = remplirTab(ensembleJoueur);

        //phase de connection des joueurs
        phaseDeConnection();
        
        if(rejouer != 2){
            //phase de longueur
            longueurPartie = scoreDeVictoire();

            //phase de coeff de temps (difficulté)
            tempsQuestions = coeffTemps();

            //creer partie
            partie = newPartie(tempsQuestions,nbJoueurs,longueurPartie);
        }
    }

    void rejouerRapide(){
        Joueur[] ensembleJoueurCopie = new Joueur[partie.nbJoueurs];
        ensembleJoueurCopie = remplirTab(ensembleJoueurCopie);
        for(int i = 0; i < length(ensembleJoueur);i++){
            ensembleJoueurCopie[i].jno = i;
            ensembleJoueurCopie[i].skin = ensembleJoueur[i].skin;
            ensembleJoueurCopie[i].nom = ensembleJoueur[i].nom;
            ensembleJoueurCopie[i].r = ensembleJoueur[i].r;
            ensembleJoueurCopie[i].v = ensembleJoueur[i].v;
            ensembleJoueurCopie[i].b = ensembleJoueur[i].b;
            ensembleJoueurCopie[i].posX = posX(i+1);
        }
        ensembleJoueur = ensembleJoueurCopie;
    }

    void phaseDeConnection(){
        for(int i = 1; i<=length(ensembleJoueur);i++){
            ligne();
            box("Connection joueur " + i + " :");
            String compteNom = connection();
            int ligne = quelLigne(compteNom);
            if(ligne != -1){
                ensembleJoueur[i-1] = newJoueur(i,
                                    charAt(ensembleSkins,StringToInt(getCell(comptes,ligne,7))),
                                    compteNom,
                                    rAssociéeAuJoueur(i),
                                    vAssociéeAuJoueur(i),
                                    bAssociéeAuJoueur(i),
                                    posX(i));
                ligne();
                box("Joueur " + i + " connecté à " + ensembleJoueur[i-1].nom +  " avec succès !");
            }else{
                ensembleJoueur[i-1] = newJoueur(i,
                                    'X',
                                    compteNom,
                                    rAssociéeAuJoueur(i),
                                    vAssociéeAuJoueur(i),
                                    bAssociéeAuJoueur(i),
                                    posX(i));
                ligne();
                box("Joueur " + i + " connecté à " + ensembleJoueur[i-1].nom +  " avec succès !");
            }
        }
    }

    String connection(){
        String saisie; //chiffre du compte... (voir derniere ligne fonction)
        do{

            ligne(); //presentation comtpes
            println("Les différents comptes :");
            for(int i = 1; i < rowCount(comptes);i++){
                if(pasDejaPris(getCell(comptes,i,0))){
                    println(i + " : " + getCell(comptes, i, 0));
                }
            }
            print("Votre choix : "); //choix (chiffre)
            saisie = readString();
            if(!(controleSaisieInt(saisie, intToString(rowCount(comptes)-1)))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
            else if(!(pasDejaPris(getCell(comptes,StringToInt(saisie),0)))){
                ligne();
                box("/!\\ Compte déja utilisé /!\\");
            }
        }while(!(controleSaisieInt(saisie, intToString(rowCount(comptes)-1)))||
                !(pasDejaPris(getCell(comptes,StringToInt(saisie),0)))); //on est sur que l'entrée est bonne


        if(!(equals(getCell(comptes,StringToInt(saisie),0),"Invité"))){ //Compte classique
            if(!(connectionMDP(StringToInt(saisie)))){
                ligne();
                box("/!\\ MDP incorrect /!\\");
                saisie = connection();
            }else{
                saisie = getCell(comptes,StringToInt(saisie),0);
            }
        }else{ //Si c'est un invité
            do{
                print("Choisissez votre pseudo : ");
                saisie = readString();
                if(equals(saisie, "")){
                    box("/!\\ Saisie incorrecte /!\\");
                }
                else if(quelLigne(saisie) != -1){
                    box("Un compte existe deja à ce nom ! Veuillez changer.");
                }
            }while(equals(saisie,"") ||
                    quelLigne(saisie) != -1);
        }
        return saisie; //...est convertie en nom du compte
    }

    int quelLigne(String nom){
        for(int i = 1; i<rowCount(comptes);i++){
            if(equals(getCell(comptes,i,0),nom)){
                return i;
            }
        }
        return -1;
    }

    boolean connectionMDP(int compte){
        boolean result = false;
        print("MDP : ");
        String mdp = readString();
        if(equals(mdp,getCell(comptes,compte,1))){
            result = true;
        }
        return result;
    }

    boolean pasDejaPris(String saisie){ //compte
        boolean result = true;
        for(int i = 0; i<length(ensembleJoueur);i++){
            if(equals(saisie,ensembleJoueur[i].nom)){
                result = false;
            }
        }
        if(equals(saisie,"Invité")){
            result = true;
        }
        return result;
    }

    Joueur[] remplirTab(Joueur[] ensembleARemplir){ //remplir tab ensemble joueur pour pv le lire
        for(int i = 0; i < length(ensembleARemplir);i++){
            ensembleARemplir[i] = newJoueur(i+1,' ',caseVide,0,0,0,0);
        }
        return ensembleARemplir;
    }

    int nbJoueurs(){
        String saisie;
        do{
            ligne();
            println("Choisissez un nombre de joueurs :\n" +
                    "- 1 joueuse(r)\n" +
                    "- 2 joueuse(r)s\n" +
                    "- 3 joueuse(r)s\n" +
                    "- 4 joueuse(r)s");
            print("Votre choix : ");
            saisie = readString();
            if(!(controleSaisieInt(saisie,"4"))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, "4")));
        return StringToInt(saisie);
    }

    int coeffTemps(){
        String saisie;
        do{
            ligne();
            println("Choisissez un temps imparti pour chaque question :\n" +
                    "- 1 : très rapide\n" +
                    "- 2 : rapide\n" +
                    "- 3 : normal\n" +
                    "- 4 : lent\n" +
                    "- 5 : très lent\n" +
                    "- 6 : temps infini\n" +
                    "(le temps s'adapte en fonction de la difficulté de la question)");
            print("Votre choix : ");
            saisie = readString();
            if(!(controleSaisieInt(saisie,"6"))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, "6")));
        box("Vous avez mis le temps au mode " + saisie + ".");
        return StringToInt(saisie);
    }

    int scoreDeVictoire(){
        String saisie;
        do{
            ligne();
            box("Choisissez un nombre de points pour gagner (entre 2 et 10) :");
            print("Votre choix : ");
            saisie = readString();
            if(equals(saisie,"")){
                saisie="11";
            }
            if(!(controleSaisieInt(saisie,"10") || StringToInt(saisie) < 2)){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(controleSaisieInt(saisie, "10")) ||
                StringToInt(saisie) < 2);
        box("Il faudra avoir " + saisie + " points pour gagner.");
        return StringToInt(saisie);
    }

    boolean commandeExterne(String saisie){
        if(equals(saisie, "T") ||
            equals(saisie, "S") ||
            equals(saisie, "U") ||
            equals(saisie, "I")){
            return true;
        }
        return false;
    }

    boolean decrypterCasesPossibles(String[] casesPossibles, String saisie){
        boolean result = false;
        for(int i = 0; i < length(casesPossibles); i++){
            if(equals(casesPossibles[i],saisie)){
                result = true;
            }
        }
        return result;
    }

    int calculTempsQuestions(int coeff, int tempsQuestion){
        return tempsQuestion * coeff + 20;
    }
    
//-------------gameplay-----------------------------------------------------------------------------

    void leJeu(){
        contenuChaqueCase = creerTabContenu(5,partie.scoreDeVictoire+1);
        contenuChaqueCase = placerJoueurs(contenuChaqueCase);
        while(scoreMax() < partie.scoreDeVictoire){
            print(clear);
            ligne();
            afficherTab(contenuChaqueCase);

            int uneCase = tourDeplacement(); //partie deplacement du tour
            if(uneCase != -1){
                int chance = (int)(random()*3);
                if(chance == 1){ //une chance sur 3 d'avoir l'item
                    ensembleJoueur[joueurActu].tools = tourItem(ensembleJoueur[joueurActu].tools, uneCase,contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].ligneItem);
                    String saisie;
                    do{
                        print("Voir (T) ou utiliser (U) l'item (rien ou autre pour ignorer) : ");
                        saisie = readString();
                        if(commandeExterne(saisie)){
                            appliquerCommandeExterne(saisie);
                        }
                    }while(commandeExterne(saisie));
                }
                println("\n");
                ligne();
                println("");
                if(uneCase != -1){
                    tourQuestion(uneCase); //partie gameplay du tour
                }
                readString();
            }

            joueurActu = (joueurActu + 1)%length(ensembleJoueur); //change joueur
        }

        tourVictoire();

        rejouer();
    }

    int tourDeplacement(){
        println("Au tour du messager " + ensembleJoueur[joueurActu].nom + " !");
        int uneCase = choixCase();
        if(uneCase != -1){
            println("\nEvenement :");
            box(contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].evenement);
        }
        return uneCase;
    }

    String tourItem(String toolsListe, int uneCase, int ligneItem){
        println("\nUn nouvel item ! :");
        int newItem = charAt(toolsListe, ligneItem)-('0');
        newItem ++;
        box(contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].apparitionItem);
        if(charAt(toolsListe,ligneItem) != 9){ //sinon ca bug
            toolsListe = substring(toolsListe, 0, ligneItem) + 
                        intToChar(newItem) +
                        substring(toolsListe, ligneItem+1, length(toolsListe));
        }
        return toolsListe;
    }

    void tourQuestion(int uneCase){
        box("Pressez \"entrée\" pour reveler la question.");
        readString();
        //debut timer
        long debut = getTime();
        String questionReponse;
        if(ensembleJoueur[joueurActu].vista){
            questionReponse = contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].question1 + "£" + contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].reponse1;
        }else{
            questionReponse = questionAleatoire(uneCase);
        }
        String question = "";
        String reponse = "";
        boolean isQuestion = true;
        for(int i = 0; i< length(questionReponse); i++){
            if(charAt(questionReponse,i) != '£' && isQuestion){
                question = question + charAt(questionReponse,i);
            }
            else if(charAt(questionReponse,i) != '£' && !isQuestion){
                reponse = reponse + charAt(questionReponse,i);  
            }else{
                isQuestion = !isQuestion;
            }
        }
        println("Question :");
        box("Question pour le messager " + 
            ensembleJoueur[joueurActu].nom + 
            " : " + 
            question);
        if(partie.coeffTemps != 6){
            box("(Vous avez " + 
                convertionEnMinute(calculTempsQuestions(partie.coeffTemps,contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].temps)) + 
                " secondes.)");
        }
        String votreReponse;
        do{
            print("Votre réponse : ");
            votreReponse = readString();
            if(equals(votreReponse,"")){
                box("/!\\ Saisie incorrecte /!\\");
            }
            else if(verifQuestion(StringToInt(votreReponse),reponse)){
                long fin = getTime();
                float tempsPrisMS = (fin - debut);
                float tempsPris = tempsPrisMS/1000;
                if(partie.coeffTemps != 6 && calculTempsQuestions(partie.coeffTemps, contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].temps) > tempsPris){
                    if(ensembleJoueur[joueurActu].score < 9){
                        println("Bien joué messager " + ensembleJoueur[joueurActu].nom + ", tu peux continuer ton chemin !");
                    }
                    ensembleJoueur[joueurActu].score = ensembleJoueur[joueurActu].score + ensembleJoueur[joueurActu].vitesse;
                    updateTab(uneCase);
                    
                    box("Vous avez mis " + tempsPris + "s à répondre.");
                }else if(partie.coeffTemps != 6){
                    box("Vous avez mis trop de temps à répondre messager. Soit " + tempsPris + " secondes.");
                }else if(partie.coeffTemps == 6){
                    ensembleJoueur[joueurActu].score = ensembleJoueur[joueurActu].score + ensembleJoueur[joueurActu].vitesse;
                    updateTab(uneCase);
                }
            }
            ensembleJoueur[joueurActu].vitesse = 1;
            clearMoai();
        }while(equals(votreReponse,""));
        long fin = getTime();
        //fin timer
    }

    String questionAleatoire(int uneCase){
        int choix = (int)(random()*3);
        if(choix == 0){
            return contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].question1 + "£" + contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].reponse1;
        }
        if(choix == 1){
            return contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].question2 + "£" + contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].reponse2;
        }
        if(choix == 2){
            return contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].question3 + "£" + contenuChaqueCase[uneCase%5][(ensembleJoueur[joueurActu].score)+(uneCase/5)].reponse3;
        }
        println("Bug : " + choix);
        return "bug";
    }

    int testAleatoire(){
        int test = (int)(random()*3+1);
        return test;
    }

    void tourVictoire(){
        print(clear);
        ligne();
        box("Bien joué ! Le messager " + ensembleJoueur[joueurActu].nom + " a atteint le score de " + partie.scoreDeVictoire + " et remporte la partie ! Félicitations !");
    }


    int choixCase(){
        String saisie;
        String[] casesPossibles = new String[]{"bloqué"};
        String[] casesPossibles2 = new String[]{"bloqué"};
        String[] casesPossibles3 = new String[]{"bloqué"}; //veut dire bug a vitesse = 4
        String[] toutesCasesPossibles;
        do{
            String texte = "Choisissez votre prochaine case : (";
            for(int i = 0; i < ensembleJoueur[joueurActu].vitesse; i++){ //génere sa vitesse
                if(i == 0){
                    casesPossibles = casesPossibles(1, ensembleJoueur[joueurActu].score+1);
                    for(int j = 0; j < length(casesPossibles); j++){
                        if(length(casesPossibles) - j == 1){
                            texte = texte + casesPossibles[j] + ")";
                        }
                        else if(length(casesPossibles) - j == 2){
                            texte = texte + casesPossibles[j] + " ou ";
                        }else{
                            texte = texte + (casesPossibles[j]) + ", ";
                        }
                    }
                }
                if(i == 1){
                    casesPossibles2 = casesPossibles(6, ensembleJoueur[joueurActu].score+1);
                    if(i == 1){
                        texte = texte + (" Ou encore devant : (");
                    }
                    for(int j = 0; j < length(casesPossibles2); j++){
                        if(length(casesPossibles2) - j == 1){
                            texte = texte + casesPossibles2[j] + ")";
                        }
                        else if(length(casesPossibles2) - j == 2){
                            texte = texte + casesPossibles2[j] + " ou ";
                        }else{
                            texte = texte + (casesPossibles2[j]) + ", ";
                        }
                    }
                }
                if(i == 2){
                    casesPossibles3 = casesPossibles(11, ensembleJoueur[joueurActu].score+1);
                    if(i > 1){
                        texte = texte +(" Ou encore plus loin ! (");
                    }
                    for(int j = 0; j < length(casesPossibles3); j++){
                        if(length(casesPossibles3) - j == 1){
                            texte = texte + casesPossibles3[j] + ")";
                        }
                        else if(length(casesPossibles3) - j == 2){
                            texte = texte + casesPossibles3[j] + " ou ";
                        }else{
                            texte = texte + (casesPossibles3[j]) + ", ";
                        }
                    }
                }
            }
            toutesCasesPossibles = new String[length(casesPossibles) + length(casesPossibles2) + length(casesPossibles3)];
            int reset = 0;
            
            for(int j = 0; j < length(casesPossibles); j++){
                toutesCasesPossibles[reset] = casesPossibles[j];
                reset ++;
            }
            for(int j = 0; j < length(casesPossibles2); j++){
                toutesCasesPossibles[reset] = casesPossibles2[j];
                reset ++;
            }
            for(int j = 0; j < length(casesPossibles3); j++){
                toutesCasesPossibles[reset] = casesPossibles3[j];  
                reset++;
            }
            reset = 0;

            if(equals(casesPossibles[0],"bloqué") && equals(casesPossibles2[0],"bloqué") && equals(casesPossibles3[0],"bloqué")){
                box("Vous n'avez nulle part ou aller messager... Prenez un peu de repos.");
                readString();
                return -1;
            }else{
                box(texte);
                println("- \"T\" pour voir vos items");
                println("- \"S\" pour voir vos stats");
                println("- \"U\" pour utiliser vos items");
            }
            saisie = readString();
            if(commandeExterne(saisie)){
                appliquerCommandeExterne(saisie);
                saisie = "9";
            }
            else if(length(saisie) != 1 || !(decrypterCasesPossibles(toutesCasesPossibles, saisie))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(decrypterCasesPossibles(toutesCasesPossibles, saisie)));
        return StringToInt(saisie)-1;
    }

    void rejouer(){
        String saisie;
        do{
        print(" -La partie est finie-\n\n" +
                    "1 - Rejouer rapide (meme parametres)\n" +
                    "2 - Rejouer semi-rapide (changer juste les joueurs)\n" +
                    "3 - Rejouer\n" +
                    "4 - Revenir à l'écran titre\n" +
                    "5 - Quitter le programme\n" +
                    "Votre choix : ");
                    saisie = readString();
            if(!(controleSaisieInt(saisie,"5"))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, "5")));
        rejouer = StringToInt(saisie);
    }


    int scoreMax(){
        int scoreMax = ensembleJoueur[0].score;
        for(int i = 1; i < partie.nbJoueurs;i++){
            if(ensembleJoueur[i].score > scoreMax){
                scoreMax = ensembleJoueur[i].score;
            }
        }
        return scoreMax;
    }
//-------------fonctions des pouvoirs/tools/items...------------------------------------------------------------------

    void appliquerCommandeExterne(String saisie){
        if(equals(saisie,"T")){
            afficherTools(ensembleJoueur[joueurActu].tools);
        }
        else if(equals(saisie,"S")){
            afficherStats(ensembleJoueur[joueurActu]);
        }
        else if(equals(saisie,"U")){
            utiliserItems(ensembleJoueur[joueurActu].tools);
        }
        else if(equals(saisie,"I")){
            //afficherRègles(); //
        }
    }

    void afficherTools(String tools){
        int compteur = 0;
        for(int i = 0; i < length(tools); i++){
            compteur = compteur + charToInt(charAt(tools,i));
        }
        ligne();
        if(compteur == 0){
            box("Vous n'avez aucun item...");
        }else{
            box("Voici les items que vous possedez : ");
            for(int i = 0; i < length(tools); i++){
                if(charAt(tools,i) != '0'){
                    println(getCell(items,i,0) + " : "); //nom"
                    box(getCell(items,i,2));  //desc    
                    println("(Possédés : " + charAt(tools,i) + ")\n\n");//quantité
                }
            }
        }
    }

    void afficherStats(Joueur joueur){
        box("Messager " + joueur.nom + " !");
        println("Actuellement, vous :");
        if(!(joueur.protégé)){
            print("N'etes pas protégé du danger");
            if(!(joueur.bloqué)){
                println(".\nMais vous n'etes pas bloqué !");
            }else{
                println(",\net vous êtes actuellement bloqué...");
            }
        }else{
            println("Êtes protégé du danger");
            if(!(joueur.bloqué)){
                println("et vous n'etes actuellement pas bloqué !");
            }else{
                println("mais vous êtes actuellement bloqué...");
            }
        }
        print("Vous pouvez avancer de " + joueur.vitesse + " case par tour");
        if(!(joueur.secondeVie)){
            println(",");
            println("et vous n'avez qu'une seule chance par question...");
        }else{
            println("\net vous avez en plus de ca une seconde chance lors de votre prochaine erreur !");
        }
        print("À et d'ailleurs, vous ");
        if(joueur.confus){
            println("êtes confus.");
        }else{
            println("n'êtes pas confus.");
        }
        
        println("De plus, vous possédez " + nombreItems() + " items.");
    }

    void utiliserItems(String itemsListe){
        if(nombreItems() == 0){
            println("Vous n'avez pas encore d'items...");
        }else{
            box("Vous pouvez utiliser :");
            utiliserItemsRecueil(itemsListe);
        }
    }

    int nombreItems(){
        int compteur = 0;
        for(int i = 0; i < length(ensembleJoueur[joueurActu].tools); i++){
            compteur = compteur + charToInt(charAt(ensembleJoueur[joueurActu].tools,i));
        }
        return compteur;
    }

    void utiliserItemsRecueil(String itemsListe){
        String saisie;
        do{
            for(int i = 0; i < length(itemsListe);i++){
                if(charAt(itemsListe,i) != '0'){
                    println((i) + " : " + getCell(items,i,0) + " (" + charAt(itemsListe,i) + " fois)");
                }
            }
            print("Que voulez vous utiliser ? (Q pour quitter) : ");
            saisie = readString();
            println("");
            if(equals(saisie,"Q")){
                return;
            }
            else if(!(controleSaisieInt(saisie,intToString(length(itemsListe))))){
                box("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(controleSaisieInt(saisie,intToString(length(itemsListe)))));


        //LANCER LES EFFETS DES ITEMS
        if(StringToInt(saisie) == 1 && charAt(itemsListe,1) != '0'){
            bocalALuciole();
        }
        else if(StringToInt(saisie) == 2 && charAt(itemsListe,2) != '0'){
            monolitheDePoche();
            int newItem = charAt(itemsListe, 2)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 2) + 
                        intToChar(newItem) +
                        substring(itemsListe, 3, length(itemsListe));
        }
        else if(StringToInt(saisie) == 3 && charAt(itemsListe,3) != '0'){
            runeDalterationRealite();
            int newItem = charAt(itemsListe, 3)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools  = substring(itemsListe, 0, 3) + 
                        intToChar(newItem) +
                        substring(itemsListe, 4, length(itemsListe));
        }
        else if(StringToInt(saisie) == 4 && charAt(itemsListe,4) != '0'){
            ailesDechuesDeLOrin();
            int newItem = charAt(itemsListe, 4)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 4) + 
                        intToChar(newItem) +
                        substring(itemsListe, 5, length(itemsListe));
        }
        else if(StringToInt(saisie) == 5 && charAt(itemsListe,5) != '0'){
            oeilOmniscientDeMiquella();
            int newItem = charAt(itemsListe, 5)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 5) + 
                        intToChar(newItem) +
                        substring(itemsListe, 6, length(itemsListe));
        }
        else if(StringToInt(saisie) == 6 && charAt(itemsListe, 6) != '0'){
            epeeMaudite();
            int newItem = charAt(itemsListe, 6)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 6) + 
                        intToChar(newItem) +
                        substring(itemsListe, 7, length(itemsListe));
        }
        else if(StringToInt(saisie) == 7 && charAt(itemsListe,7) != '0'){
            aiguilleDeGivre();
            int newItem = charAt(itemsListe, 7)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 7) + 
                        intToChar(newItem) +
                        substring(itemsListe, 8, length(itemsListe));
        }
        else if(StringToInt(saisie) == 8 && charAt(itemsListe, 8) != '0'){
            benedictionDeMiquella();
            int newItem = charAt(itemsListe, 8)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 8) + 
                        intToChar(newItem) +
                        substring(itemsListe, 9, length(itemsListe));
        }
        else if(StringToInt(saisie) == 9 && charAt(itemsListe,9) != '0'){
            mineraiDeFer();
            int newItem = charAt(itemsListe, 9)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 9) + 
                        intToChar(newItem) +
                        substring(itemsListe, 10, length(itemsListe));
                        println(substring(itemsListe, 0, 0) + "/" + intToChar(newItem) + "/" + substring(itemsListe, 1, length(itemsListe)));
        }
        else if(StringToInt(saisie) == 10 && charAt(itemsListe,10) != '0'){
            runeDalterationIntimidation();
            int newItem = charAt(itemsListe, 10)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 10) + 
                        intToChar(newItem) +
                        substring(itemsListe, 11, length(itemsListe));
        }
        else if(StringToInt(saisie) == 11 && charAt(itemsListe,11) != '0'){
            runeDalterationTemps();
            int newItem = charAt(itemsListe, 11)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 11) + 
                        intToChar(newItem) +
                        substring(itemsListe, 12, length(itemsListe));
        }
        else if(StringToInt(saisie) == 12 && charAt(itemsListe,12) != '0'){
            viandeDeKrah();
            int newItem = charAt(itemsListe, 12)-('0');
            newItem --;
            ensembleJoueur[joueurActu].tools = substring(itemsListe, 0, 12) + 
                        intToChar(newItem) +
                        substring(itemsListe, 13, length(itemsListe));
        }else{
            ligne();
            println("Vous n'avez pas cet objet.");
        }

    }
//--------------les pouvoirs-------------------------------------------------------------------------------------------
    void bocalALuciole(){ //1
        if(!(ensembleJoueur[joueurActu].protégé) && !(ensembleJoueur[joueurActu].protégéMiquella) && !(ensembleJoueur[joueurActu].protégéMineraisDeFer)){
            ensembleJoueur[joueurActu].protégé = true;
            int newItem = charAt(ensembleJoueur[joueurActu].tools, 1)-('0');
            newItem --; 
            ensembleJoueur[joueurActu].tools = substring(ensembleJoueur[joueurActu].tools, 0, 1) + 
                        intToChar(newItem) +
                        substring(ensembleJoueur[joueurActu].tools, 2, length(ensembleJoueur[joueurActu].tools));
            box("Vous êtes désormais protégé du danger pour votre prochain tour !");
        }else{
            box("Vous êtes actuellement déja protégé du danger !");
        }
    }
    
    void monolitheDePoche(){ //2
        box("Voici toutes les cases ou vous pouvez placer le monolithe :");
        String[] ensembleCasesDerriere = new String[]{"16"};
        if(ensembleJoueur[joueurActu].score > 0){
            println("Sur les cases derriere vous : ");
            ensembleCasesDerriere = casesPossibles(1, ensembleJoueur[joueurActu].score-1);
            for(int i = 0; i < length(ensembleCasesDerriere); i++){
                print(ensembleCasesDerriere[i] + ", ");
            }
            println();
        }
        println("Sur les cases sur votre ligne : ");
        String[] ensembleCasesMaLigne = casesPossibles(6, ensembleJoueur[joueurActu].score);
        for(int i = 0; i < length(ensembleCasesMaLigne); i++){
                print(ensembleCasesMaLigne[i] + ", ");
            }
        println();
        
        println("Sur les cases devant vous : ");
        String[] ensembleCasesDevant = casesPossibles(11, ensembleJoueur[joueurActu].score+1);
        for(int i = 0; i < length(ensembleCasesDevant); i++){
                print(ensembleCasesDevant[i] + ", ");
            }
        println();

        String saisie;
        do{
            print("Choisissez une case parmi celles-ci (ou Q pour quitter) : ");
            saisie = readString();
            if(equals(saisie,"Q")){
                return;
            }
            else if(!(controleSaisieInt(saisie,"15")) ||
                    !(decrypterCasesPossibles(ensembleCasesDerriere, saisie)) &&
                    !(decrypterCasesPossibles(ensembleCasesMaLigne, saisie)) &&
                    !(decrypterCasesPossibles(ensembleCasesDevant, saisie))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie,"15")) ||
                !(decrypterCasesPossibles(ensembleCasesDerriere, saisie)) &&
                !(decrypterCasesPossibles(ensembleCasesMaLigne, saisie)) &&
                !(decrypterCasesPossibles(ensembleCasesDevant, saisie)));
        
        contenuChaqueCase[(StringToInt(saisie)%5)-1][(ensembleJoueur[joueurActu].score-1)+(StringToInt(saisie)-1)/5].remplirCase = caseMonolith;
        contenuChaqueCase[(StringToInt(saisie)%5)-1][(ensembleJoueur[joueurActu].score-1)+(StringToInt(saisie)-1)/5].possedeMoai = true;
        contenuChaqueCase[(StringToInt(saisie)%5)-1][(ensembleJoueur[joueurActu].score-1)+(StringToInt(saisie)-1)/5].coupableMoai = ensembleJoueur[joueurActu].jno;
        box("Votre monolithe a été placé avec succès !!");
        readString();
        print(clear);
        afficherTab(contenuChaqueCase);
    }

    String[] casesPossibles(int incrémentation, int colonne){
        String[] ensembleCases;
        int cpt = 0;
        for(int i = 0; i < 5; i++){
            if(equals(contenuChaqueCase[i][colonne].remplirCase,caseVide)){
                if(i == ensembleJoueur[joueurActu].posX ||
                    i == ensembleJoueur[joueurActu].posX-1 ||
                    i == ensembleJoueur[joueurActu].posX+1){
                    cpt++;
                }
            }
        }
        if(cpt != 0){
            ensembleCases = new String[cpt];
            cpt = 0;
            for(int i = 0; i < 5; i++){
                if(equals(contenuChaqueCase[i][colonne].remplirCase,caseVide)){
                    if(i == ensembleJoueur[joueurActu].posX ||
                        i == ensembleJoueur[joueurActu].posX-1 ||
                        i == ensembleJoueur[joueurActu].posX+1){
                        ensembleCases[cpt] = "" + (i+incrémentation);
                        cpt++;
                    }
                }
            }
        }else{
            ensembleCases = new String[]{"bloqué"};
        }
        return ensembleCases;
    }

    void runeDalterationRealite(){ //3

    }

    void ailesDechuesDeLOrin(){ //4
        if(ensembleJoueur[joueurActu].vitesse + ensembleJoueur[joueurActu].score == partie.scoreDeVictoire){
            box("M'enfin ! Si proche de l'arrivée... faite le a la loyale, pardi !");
        }else if(ensembleJoueur[joueurActu].vitesse + ensembleJoueur[joueurActu].score > partie.scoreDeVictoire){
            println("Premier degres vous n'etes pas sensé etre la. Si vous voyez ce message, le programme va crash et vous avez trouvé un bug. GG lol");
        }else{
            ensembleJoueur[joueurActu].vitesse += 1;
            box("D'un pelage si doux et pourtant si intimidant, vous parvenez à enfiler ces ailes d'Ørin, étonnement plutot ergonomique. Ce don venant d'un être à la pointe de l'évolution, de ce rapace concidéré comme un divinité par sa perfection,  vous permettera d'elever votre a " + ensembleJoueur[joueurActu].vitesse + " jusqu'au prochain tour.");
        }

    }

    void oeilOmniscientDeMiquella(){ //5
        ensembleJoueur[joueurActu].vista = true;
        String saisie;
        String[] casesPossibles = casesPossibles(1,ensembleJoueur[joueurActu].score+1);
        do{
            print("De quelle case voulez-vous voir l'avenir ? : ");
            saisie = readString();
            if(!(decrypterCasesPossibles(casesPossibles, saisie))){
                box("/!\\ saisie incorrecte /!\\");
            }
        }while(!(decrypterCasesPossibles(casesPossibles, saisie)));
        println("La question sur cette case : " + contenuChaqueCase[StringToInt(saisie)][ensembleJoueur[joueurActu].score+1].question1);
    }

    void epeeMaudite(){

    }

    void aiguilleDeGivre(){

    }

    void benedictionDeMiquella(){ //8
        if(!(ensembleJoueur[joueurActu].protégé) && !(ensembleJoueur[joueurActu].protégéMiquella) && !(ensembleJoueur[joueurActu].protégéMineraisDeFer)){
            ensembleJoueur[joueurActu].protégéMiquella = true;
            int newItem = charAt(ensembleJoueur[joueurActu].tools, 8)-('0');
            newItem --; 
            ensembleJoueur[joueurActu].tools = substring(ensembleJoueur[joueurActu].tools, 0, 8) + 
                        intToChar(newItem) +
                        substring(ensembleJoueur[joueurActu].tools, 9, length(ensembleJoueur[joueurActu].tools));
            box("La bénédiction de Miquella vous aquiert une protection totale durant les 2 prochains tours !");
        }else{
            box("Vous êtes actuellement déja protégé du danger !");
        }

    }

    void mineraiDeFer(){ //9
        if(!(ensembleJoueur[joueurActu].protégé) && !(ensembleJoueur[joueurActu].protégéMiquella) && !(ensembleJoueur[joueurActu].protégéMineraisDeFer)){
            ensembleJoueur[joueurActu].protégéMineraisDeFer = true;
            int newItem = charAt(ensembleJoueur[joueurActu].tools, 9)-('0');
            newItem --; 
            ensembleJoueur[joueurActu].tools = substring(ensembleJoueur[joueurActu].tools, 0, 9) + 
                        intToChar(newItem) +
                        substring(ensembleJoueur[joueurActu].tools, 10, length(ensembleJoueur[joueurActu].tools));
            box("Vous parvenez, étonnement, a faire de ce minerai de fer un bouclier de dernier recours. Vous etes protégé jusqu'au prochain choc.");
        }else{
            box("Vous êtes actuellement déja protégé du danger !");
        }
    }

    void runeDalterationIntimidation(){

    }

    void runeDalterationTemps(){

    }

    void viandeDeKrah(){

    }
//-------------tableau de jeu--------------------------------------------------------------------------

    ContenuCases[][] creerTabContenu(int lignes, int colonnes){
        ContenuCases[][] Tcreer = new ContenuCases[lignes][colonnes];
        for(int i = 0; i < lignes; i++){
            for(int j = 0; j < colonnes; j++){

                // Tirage de la zone
                int ligneZone = (int)(random() * (rowCount(zones) - 1)) + 1;
                String zone = getCell(zones, ligneZone, 0);

                // Tirage d’un événement COMPATIBLE avec la zone
                int ligneEvenement;
                String zoneEvenement;
                do{
                    ligneEvenement = (int)(random() * (rowCount(evenements) - 1)) + 1;
                    zoneEvenement = getCell(evenements, ligneEvenement, 3);
                }while(!equals(zoneEvenement, zone));

                String descriptionEvenement = getCell(evenements, ligneEvenement, 2);
                String nomEvenement = getCell(evenements, ligneEvenement, 1);

                // Tirage d’une question COMPATIBLE avec l’événement
                int ligneQuestion;
                String evenementQuestion;
                do{
                    ligneQuestion = (int)(random() * (rowCount(questions) - 1)) + 1;
                    evenementQuestion = getCell(questions, ligneQuestion, 4);
                }while(!equals(evenementQuestion, nomEvenement));

                // Tirage d’une question2 COMPATIBLE avec l’événement
                int ligneQuestion2;
                String evenementQuestion2;
                do{
                    ligneQuestion2 = (int)(random() * (rowCount(questions) - 1)) + 1;
                    evenementQuestion2 = getCell(questions, ligneQuestion2, 4);
                }while(!equals(evenementQuestion2, nomEvenement) ||
                            ligneQuestion2 == ligneQuestion);

                // Tirage d’une question3 COMPATIBLE avec l’événement
                int ligneQuestion3;
                String evenementQuestion3;
                do{
                    ligneQuestion3 = (int)(random() * (rowCount(questions) - 1)) + 1;
                    evenementQuestion3 = getCell(questions, ligneQuestion3, 4);
                }while(!equals(evenementQuestion3, nomEvenement) || 
                            ligneQuestion3 == ligneQuestion2 ||
                            ligneQuestion3 == ligneQuestion);

                // Tirage d’un item COMPATIBLE avec l’événement
                int ligneItem;
                String evenementItem;
                do{
                    ligneItem = (int)(random() * (rowCount(items) - 1)) + 1;
                    evenementItem = getCell(items, ligneItem, 6);
                }while(!equals(evenementItem, nomEvenement));


                // Création du contenu
                Tcreer[i][j] = newContenuCases(
                                descriptionEvenement,
                                getCell(questions, ligneQuestion, 2),
                                getCell(questions, ligneQuestion2, 2),
                                getCell(questions, ligneQuestion3, 2),
                                getCell(items, ligneItem, 1),
                                getCell(items, ligneItem, 2),
                                ligneItem,
                                StringToInt(getCell(questions, ligneQuestion, 3)),
                                StringToInt(getCell(questions, ligneQuestion2, 3)),
                                StringToInt(getCell(questions, ligneQuestion3, 3)),

                                StringToInt(getCell(questions, ligneQuestion, 5)));
            }
        }
        readString();
        return Tcreer;
    }

    ContenuCases[][] placerJoueurs(ContenuCases[][] tab2){
        caseJoueur = rgb(ensembleJoueur[joueurActu].r,
                                                        ensembleJoueur[joueurActu].v,
                                                        ensembleJoueur[joueurActu].b,false) 
                                                        + " " + ensembleJoueur[joueurActu].skin + " " + RESET;
        caseJoueur2 = rgb(ensembleJoueur[(joueurActu+1)%length(ensembleJoueur)].r,ensembleJoueur[(joueurActu+1)%length(ensembleJoueur)].v, ensembleJoueur[(joueurActu+1)%length(ensembleJoueur)].b,false) + " " + ensembleJoueur[(joueurActu+1)%length(ensembleJoueur)].skin + " " + RESET;
        caseJoueur3 = rgb(ensembleJoueur[(joueurActu+2)%length(ensembleJoueur)].r,ensembleJoueur[(joueurActu+2)%length(ensembleJoueur)].v, ensembleJoueur[(joueurActu+2)%length(ensembleJoueur)].b,false) + " " + ensembleJoueur[(joueurActu+2)%length(ensembleJoueur)].skin + " " + RESET;
        caseJoueur4 = rgb(ensembleJoueur[(joueurActu+3)%length(ensembleJoueur)].r,ensembleJoueur[(joueurActu+3)%length(ensembleJoueur)].v, ensembleJoueur[(joueurActu+3)%length(ensembleJoueur)].b,false) + " " + ensembleJoueur[(joueurActu+3)%length(ensembleJoueur)].skin + " " + RESET;

        ContenuCases tab[][] = tab2;
        if(length(ensembleJoueur) == 1){
            tab[2][0].remplirCase = caseJoueur;
        }
        if(length(ensembleJoueur) == 2){
            tab[1][0].remplirCase = caseJoueur;
            tab[3][0].remplirCase = caseJoueur2;
        }
        if(length(ensembleJoueur) == 3){
            tab[0][0].remplirCase = caseJoueur;
            tab[2][0].remplirCase = caseJoueur2;
            tab[4][0].remplirCase = caseJoueur3;
        }
        if(length(ensembleJoueur) == 4){
            tab[0][0].remplirCase = caseJoueur;
            tab[1][0].remplirCase = caseJoueur2;          
            tab[2][0].remplirCase = caseJoueur3;
            tab[3][0].remplirCase = caseJoueur4;
        }
        return tab;
    }

    void dessineBordCaseHaut (int m){
        print("╔");
        for(int j =0;j<m-1;j++){
            print("═══");
            print('╤');
        }
        print("═══");
        print("╗");
        println("");
    }
//╔ ╗ ╚ ╝ ═ ║ ╦ ╩ ╠ ╣ ╬.   ╟   ╢.   ┌ ┐ └ ┘ ─ │ ┬ ┴ ├ ┤ ┼ ╤ ╧
    void dessineContenuCase(int m, int ligne, ContenuCases[][] tab){
        print('║');
        for(int col=0; col<m-1; col++){
            print(tab[ligne][col].remplirCase);
            print('│');
        }
        print(tab[ligne][m-1].remplirCase);
        print('║');
        println("");
    }

    void dessineMidCase(int m){
        print("╟");
        for(int j =0;j<m-1;j++){
            print("───");
            print('┼');
        }
        print("───");
        print("╢");
        println("");
    }

    void dessineBordCaseBas (int m){
        print("╚");
        for(int j =0;j<m-1;j++){
            print("═══");
            print('╧');
        }
        print("═══");
        print("╝");
        println("");
    }

    void afficherTab(ContenuCases[][] t) {
        dessineBordCaseHaut(length(t,2)-1);
        dessineContenuCase(length(t,2)-1,0,t);
        for(int i=1;i < length(t,1);i++){
            dessineMidCase(length(t,2)-1);
            dessineContenuCase(length(t,2)-1,i,t);
        }
        dessineBordCaseBas(length(t,2)-1);
    }

    void updateTab(int uneCase){
        contenuChaqueCase[uneCase%5][ensembleJoueur[joueurActu].score].remplirCase = caseJoueur; 
        readString();
        contenuChaqueCase[ensembleJoueur[joueurActu].posX][ensembleJoueur[joueurActu].score-ensembleJoueur[joueurActu].vitesse].remplirCase = caseVide;
        ensembleJoueur[joueurActu].posX = uneCase%5;
    }

    void clearMoai(){   
        /*clear moai processus :
                regarder si moai est présent. Si oui :
                    regarder coupable. si coupable n'a pas "placé moai" :
                        alors il VIENT de le placer, donc lui donner "placé moai".
                    si a la place il a bien "placé moai", 
                        alors on verif si c'est son tour. Si oui, 
                            alors le moai est clear, et son "placé moai".
                */
        for(int i = 0; i < length(contenuChaqueCase,1); i++){
            for(int j = 0; j < length(contenuChaqueCase,2); j++){
                if(contenuChaqueCase[i][j].possedeMoai){
                    if(ensembleJoueur[contenuChaqueCase[i][j].coupableMoai].moaiPlacé == false){
                        ensembleJoueur[contenuChaqueCase[i][j].coupableMoai].moaiPlacé = true;
                    }else{
                        if(ensembleJoueur[contenuChaqueCase[i][j].coupableMoai].jno == joueurActu){
                            contenuChaqueCase[i][j].remplirCase = caseVide;
                            ensembleJoueur[joueurActu].moaiPlacé = false;
                        }
                    }
                }
            }
        }
    }

    void clearProtégé(){
        if(ensembleJoueur[joueurActu].protégé == true){
            if(ensembleJoueur[joueurActu].protégéBoiteALuciolePrisEnCompte == false){
                ensembleJoueur[joueurActu].protégéBoiteALuciolePrisEnCompte = true;
            }else{
                ensembleJoueur[joueurActu].protégé = true;
                ensembleJoueur[joueurActu].protégéBoiteALuciolePrisEnCompte = false;
            }
        }
        if(ensembleJoueur[joueurActu].protégéMiquella == true){
            if(ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte == 2){
                ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte = 1;
            }
            else if(ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte == 1){
                ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte = 0;
                ensembleJoueur[joueurActu].protégéMiquella = false;
            }
            else if(ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte == 0){
                ensembleJoueur[joueurActu].protégéMiquellaPrisEnCompte = 2;
            }
        }
    }
//-------------autre------------------------------------------------------------------------------------------------------

    boolean verifQuestion(int saisie, String reponse){
        if(saisie == StringToInt(reponse)){
            return true;
        }else{
            println("Je suis désolé messager " + ensembleJoueur[joueurActu].nom + ", tu vas devoir rester un moment dans cet endroit.");
            return false;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////_______  /////////////////////////////////////////////////////////////////
//////////////////////////////|comptes| /////////////////////////////////////////////////////////////////
//////////////////////////////|_______| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
    
    void listeCompte(){
        String saisie = bonneEntree(); //chiffre du compte... (voir derniere ligne fonction)
        //acces aux comptes
        if(StringToInt(saisie) < rowCount(comptes)-1){
            accesAuxComptes(saisie);
        }
        //creer un compte
        else if(StringToInt(saisie) == rowCount(comptes)-1){
            creerUnCompte(saisie);
        }
        print(clear);
    }

    String bonneEntree(){
        String saisie;
        do{

            ligne(); //presentation comptes
            println("Les différents comptes :");
            for(int i = 1; i < rowCount(comptes)-1;i++){
                println(" - " + i + " : " + getCell(comptes, i, 0));
            }
            println(" - " + (rowCount(comptes)-1) + " : Créer un compte");
            println(" - " + rowCount(comptes) + " : Revenir en arrière");

            print("Votre choix : "); //choix (chiffre)
            saisie = readString();
            if(!(controleSaisieInt(saisie, intToString(rowCount(comptes))))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "99";
            }
        }while(!(controleSaisieInt(saisie, intToString(rowCount(comptes))))); //on est sur que l'entrée est bonne
        return saisie;
    }

    void accesAuxComptes(String saisie){
        if(!(connectionMDP(StringToInt(saisie)))){
            ligne();
            box("/!\\ MDP incorrect /!\\");
            listeCompte();
        }else{
            choixVerifCompte(saisie);
        }
        
    }

    void creerUnCompte(String saisie){
        String[][] nouveauCompte = new String[rowCount(comptes) + 1][columnCount(comptes)];
        for(int i = 0; i < rowCount(comptes)-1; i++){
            for(int j = 0; j < columnCount(comptes);j++){
                nouveauCompte[i][j] = getCell(comptes,i,j);
            }
        }
        String newName = "";
        String newMDP = "";
        do{
            print("Choisissez le nom de votre compte : ");
            newName = readString();
            nouveauCompte[length(nouveauCompte,1)-2][0] = newName;
            if(length(newName) < 2){
                println("Nom trop court. Recommencez. (2 car min)");
            }
            else if(quelLigne(newName) != -1){
                println("Ce compte existe deja !");
            }else{
                print("Choisissez le MDP de votre compte : ");
                newMDP = readString();
                nouveauCompte[length(nouveauCompte,1)-2][1] = newMDP;
                if(length(newMDP) < 3){
                    println("MDP trop court. Recommencez. (3 car min)");
                    readString();
                }
            }
        }while(length(newName) < 2 ||
                length(newMDP) < 3 ||
                quelLigne(newName) != -1);

        nouveauCompte[length(nouveauCompte,1)-2][2] = "0";
        nouveauCompte[length(nouveauCompte,1)-2][3] = "0";
        nouveauCompte[length(nouveauCompte,1)-2][4] = "0";
        nouveauCompte[length(nouveauCompte,1)-2][5] = "0";
        nouveauCompte[length(nouveauCompte,1)-2][6] = "1000000000000000000000000";
        nouveauCompte[length(nouveauCompte,1)-2][7] = "0";
        for(int j = 0; j < length(nouveauCompte, 2);j++){
            nouveauCompte[rowCount(comptes)][j] = getCell(comptes, rowCount(comptes)-1,j);
        }
        saveCSV(nouveauCompte, "CSV/comptes.CSV");
        comptes = loadCSV("CSV/comptes.CSV");
        box("Nouveau compte crée avec succès !");
        readString();
        print(clear);
        listeCompte();
    }

    void choixVerifCompte(String compte){
        String saisie;
        do{
            ligne();
            box("Bienvenue " + getCell(comptes, StringToInt(compte), 0) + " !");
            ligne();
            println("Que voulez vous faire ? :\n" +
                    "- 1 : regarder vos stats\n" +
                    "- 2 : changer votre nom\n" +
                    "- 3 : changer votre mdp\n" +
                    "- 4 : changer de skin\n" +
                    "- 5 : revenir en arriere\n" +
                    "- 6 : supprimer votre compte\n");
            print("Votre choix : ");
            saisie = readString();
            if(!(controleSaisieInt(saisie,"6"))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, "6")));

        if(StringToInt(saisie) == 1){ //voir stats
            stats(compte);
        }

        if(StringToInt(saisie) == 2){ //changer nom
            changerNom(compte);
        }

        if(StringToInt(saisie) == 3){ //changer mdp
            changerMDP(compte);
        }

        if(StringToInt(saisie) == 4){ //skin
            changerSkin(compte);
        }

        if(StringToInt(saisie) == 5){ //retour
            listeCompte();
            print(clear);
        }

        if(StringToInt(saisie) == 6){ //delete
            deleteCompte(compte);
        }
    }

    void stats(String compte){
        float victoires = StringToInt(getCell(comptes, StringToInt(compte), 4));
        float defaites = StringToInt(getCell(comptes, StringToInt(compte), 5));
        float wr = (victoires / (victoires + defaites)) * 100;
        box("Vous avez gagné " + getCell(comptes, StringToInt(compte), 4) +
            " fois et perdu " + getCell(comptes, StringToInt(compte), 5) + " fois. " +
            "Pour un total de " + wr + "% de taux de victoires.");
        box("De plus, vous êtes niveau " + 
            getCell(comptes, StringToInt(compte), 2) + 
            " avec " + 
            getCell(comptes, StringToInt(compte), 3) + 
            "XP.");
        print("Entrez pour continuer.");
        readString();
        print(clear);
        choixVerifCompte(compte);
    }

    void changerNom(String compte){
        ligne();
        String[][] lesNoms = new String[rowCount(comptes)][columnCount(comptes)];
        for(int i = 0; i < length(lesNoms,1);i++){
            for(int j = 0; j < length(lesNoms,2);j++){
                lesNoms[i][j] = getCell(comptes,i,j);
            }
        }
        box("Changer de nom :");
        String nouveauNom;
        do{
            print("Nouveau nom  : ");
            nouveauNom = readString();
            if(length(nouveauNom) < 2){
                println("Nom trop court. Recommencez. (2 car min)");
            }
            else if(quelLigne(nouveauNom) != -1){
                println("Ce compte existe deja !");
            }
        }while(length(nouveauNom) < 2 || quelLigne(nouveauNom) != -1);
        lesNoms[StringToInt(compte)][0] = nouveauNom;
        saveCSV(lesNoms, "CSV/comptes.CSV");
        comptes = loadCSV("CSV/comptes.CSV");
        box("Nom changé avec succès !");
        readString();
        print(clear);
        choixVerifCompte(compte);
    }

    void changerMDP(String compte){
        String nouveauMDP;
        String ConfirmezMDP;
        do{
            ligne();
            String[][] lesMDP = new String[rowCount(comptes)][columnCount(comptes)];
            for(int i = 0; i < length(lesMDP,1);i++){
                for(int j = 0; j < length(lesMDP,2);j++){
                    lesMDP[i][j] = getCell(comptes,i,j);
                }
            }
            box("Changer de mot de passe");
            do{
                print("Nouveau mot de passe : ");
                nouveauMDP = readString();
                print("Confirmez le mot de passe : ");
                ConfirmezMDP = readString();
                
                if(length(nouveauMDP) < 2){
                    println("MDP trop court. Recommencez. (3 car min)");
                }
            }while(length(nouveauMDP) < 3);

            if(equals(nouveauMDP, ConfirmezMDP)){
                lesMDP[StringToInt(compte)][1] = nouveauMDP;
                saveCSV(lesMDP, "CSV/comptes.CSV");
                comptes = loadCSV("CSV/comptes.CSV");
                box("Mot de passe changé avec succès !");
            }else{
                box("Les mots de passe ne correspondent pas.");
            }
        }while(!(equals(nouveauMDP, ConfirmezMDP)));
        readString();
        print(clear);
        choixVerifCompte(compte);
    }

    void changerSkin(String compte){
        String choisirSkin;
        lesSkins(getCell(comptes,StringToInt(compte),6));
        do{
            ligne();
            box("Changer de skin :");
            print("Quel skin voulez-vous porter ? : ");
            choisirSkin = readString();
            if(equals(choisirSkin,"") || !(controleSaisieSkin(choisirSkin, getCell(comptes, StringToInt(compte),6)))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                choisirSkin = intToString(length(ensembleSkins) + 1);
            }
        }while(!(controleSaisieSkin(choisirSkin, getCell(comptes, StringToInt(compte),6))));

        String[][] lesSkins = new String[rowCount(comptes)][columnCount(comptes)];
        for(int i = 0; i < length(lesSkins,1);i++){
            for(int j = 0; j < length(lesSkins,2);j++){
                lesSkins[i][j] = getCell(comptes,i,j);
            }
        }
        lesSkins[StringToInt(compte)][7] = intToString(StringToInt(choisirSkin)-1);
        saveCSV(lesSkins, "CSV/comptes.CSV");
        comptes = loadCSV("CSV/comptes.CSV");
        box("Skin changé avec succès !");
        readString();
        print(clear);
        choixVerifCompte(compte);
    }

    void deleteCompte(String compte){
        ligne();
        String[][] lesComptes = new String[rowCount(comptes)-1][columnCount(comptes)];
        for(int i = 0; i < length(lesComptes,1);i++){
            for(int j = 0; j < length(lesComptes,2);j++){
                lesComptes[i][j] = getCell(comptes,i,j);
            }
        }
        box("Supprimer votre compte :");
        print("Êtes vous sûr de vouloir supprimer votre compte ? (O/N) : ");

        String confirmation = readString();
        if(equals(confirmation,"O")){
            for(int i = StringToInt(compte); i < length(lesComptes,1);i++){
                lesComptes[i][0] = getCell(comptes,i+1,0);
                lesComptes[i][1] = getCell(comptes,i+1,1);
                lesComptes[i][2] = getCell(comptes,i+1,2);
                lesComptes[i][3] = getCell(comptes,i+1,3);
                lesComptes[i][4] = getCell(comptes,i+1,4);
                lesComptes[i][5] = getCell(comptes,i+1,5);
            }

            saveCSV(lesComptes, "CSV/comptes.CSV");
            comptes = loadCSV("CSV/comptes.CSV");
            box("Compte supprimé avec succès.");
        }else{
            box("Suppression annulée.");
        }
        readString();
        print(clear);
        listeCompte();
    }

    boolean controleSaisieSkin(String saisie, String skinsPossedes){
        if(StringToInt(saisie) == 0 || StringToInt(saisie) > length(skinsPossedes)){
            return false;
        }
        if(charAt(skinsPossedes, StringToInt(saisie)-1) == '1'){
            return true;
        }else{
            return false;
        }
    }

    void lesSkins(String listeDesSkins){
        int compteur = 0;
        for(int i = 0; i < length(listeDesSkins);i++){
            if(charAt(listeDesSkins, i) == '1'){
                if(i == 0){
                    println("Vous avez débloqué les skins suivants :");
                }
                println("Skin " + (i+1) + " : " + charAt(ensembleSkins,i));
                compteur++;
            }
        }
        if (compteur == 0){
            println("Vous n'avez débloqué aucun skin...");
        }
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////______  /////////////////////////////////////////////////////////////////
//////////////////////////////|règles| /////////////////////////////////////////////////////////////////
//////////////////////////////|______| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////




/////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////_____ /////////////////////////////////////////////////////////////////
//////////////////////////////|tests| /////////////////////////////////////////////////////////////////
//////////////////////////////|_____| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------grub-----------------------------------------------------------------------------

    //aucune a tester

//-------------générales-----------------------------------------------------------------------------
    void test_controleSaisieInt(){
        assertTrue(controleSaisieInt("2", "3"));
        assertTrue(controleSaisieInt("1","3"));
        assertTrue(controleSaisieInt("3","3"));
        assertFalse(controleSaisieInt("4","3"));
        assertFalse(controleSaisieInt("0","3"));
        assertFalse(controleSaisieInt("a","2545"));
        assertFalse(controleSaisieInt("","28"));

    }

    void test_StringToInt(){
        assertEquals(StringToInt("120"),120);
        assertEquals(StringToInt("12345"),12345);
        assertEquals(StringToInt("13"),13);
        assertEquals(StringToInt("9"),9);
        assertEquals(StringToInt("520"),520);
        assertEquals(StringToInt("416516198"),416516198);
        assertEquals(StringToInt("053"),53);
    }
    
    void test_intToChar(){
        assertEquals(intToChar(5),'5');
        assertEquals(intToChar(9),'9');
        assertEquals(intToChar(2),'2');
        assertEquals(intToChar(0),'0');
    }

    void test_newJoueur(){
        Joueur j = new Joueur();
        j.jno = 3;
        j.nom = "test";
        assertEquals(newJoueur(3,' ',"test",0,0,0,0).jno,j.jno);
        assertEquals(newJoueur(3,' ',"test",0,0,0,0).nom,j.nom);
    }

    void test_newPartie(){
        Partie p = new Partie();
        p.coeffTemps = 1;
        p.nbJoueurs = 3;
        p.scoreDeVictoire = 10;
        assertEquals(newPartie(1,3,10).coeffTemps,p.coeffTemps);
        assertEquals(newPartie(1,3,10).nbJoueurs,p.nbJoueurs);
        assertEquals(newPartie(1,3,10).scoreDeVictoire,p.scoreDeVictoire);
    }

    void test_controleSaisieSkin(){
        assertTrue(controleSaisieSkin("1", "1101101000010110100001000"));
        assertTrue(controleSaisieSkin("5", "1101101000010110100001000"));
        assertTrue(controleSaisieSkin("7", "1101101000010110100001000"));
        assertTrue(controleSaisieSkin("14", "1101101000010110100001000"));
        assertTrue(controleSaisieSkin("15", "1101101000010110100001000"));
        assertFalse(controleSaisieSkin("16", "1101101000010110100001000"));
        assertFalse(controleSaisieSkin("21", "1101101000010110100001000"));

    }

    void test_quelLigne(){
        assertEquals(quelLigne("Tom"),1);
        assertEquals(quelLigne("papaz"),-1);
        assertEquals(quelLigne("boneva"),-1);
        assertEquals(quelLigne("Robin"),4);
    }

    void test_testAleatoire(){
        boolean test = false;
        for(int i = 0; i < 100; i++){
            print(testAleatoire());
            if(testAleatoire() == 0){
                test = true;
            }
        }
        assertFalse(test);
 
        test = false;
        for(int i = 0; i < 100; i++){
            if(testAleatoire() == 1){
                test = true;
            }
        }
        assertTrue(test);
 
        test = false;
        for(int i = 0; i < 100; i++){
            if(testAleatoire() == 2){
                test = true;
            }
        }
        assertTrue(test);
 
        test = false;
        for(int i = 0; i < 100; i++){
            if(testAleatoire() == 3){
                test = true;
            }
        }
        assertTrue(test);

        test = false;
        for(int i = 0; i < 100; i++){
            if(testAleatoire() == 4){
                test = true;
            }
        }
        assertFalse(test);
    }

    void test_ConvertioEnMinute(){
        assertEquals(convertionEnMinute(39),"39");
        assertEquals(convertionEnMinute(59),"59");
        assertEquals(convertionEnMinute(60),"1 minutes et 0");
        assertEquals(convertionEnMinute(92),"1 minutes et 32");
        assertEquals(convertionEnMinute(182),"3 minutes et 2");
    }
//-------------jouer-----------------------------------------------------------------------------


//-------------connection-----------------------------------------------------------------------------


//-------------règles-----------------------------------------------------------------------------


}