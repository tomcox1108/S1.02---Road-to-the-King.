/* 
TODO : 
- rédiger et implémenter les règles
- mettre plus de questions aux CSV (plus d'event aussi)
- gerer le truc de temps infini
- //implementer la fonction de save a la fin de chaque game
- implémenter la fonction d'objets (simple)
- //implémenter les boss ?
- faire en sorte de choisir la longueur des parties
- tester les bugs a la fin de la games (new games and stuff)
*/

import extensions.File;
import extensions.CSVFile;
class RoadToTheKing extends Program{
//variables globales
final String clear = "\033[H\033[2J\033[3J";
final CSVFile comptes = loadCSV("CSV/comptes.CSV");
final CSVFile dilemme = loadCSV("CSV/dilemmes.CSV",'_');
final CSVFile evenements = loadCSV("CSV/evenements.CSV",'_');
final CSVFile items = loadCSV("CSV/items.CSV",'_');
final CSVFile questions = loadCSV("CSV/questions.CSV",'_');
final CSVFile zones = loadCSV("CSV/zones.CSV",'_');
final String ensembleSkins = "0⩇⁝⬔🗝⧖⚙𝓩𝓐⚖ڽ﷼⚔✟✠☠⚡︎⌘⏀⛩☯✧❂*☭";
Joueur[] ensembleJoueur; //trouver joueur actu et load les données dans les bonnes cases
ContenuCases[][] contenuChaqueCase; 
Partie partie;
int joueurActu = 0; //joueur actuel
int rejouer = 3;

//-------------programme-----------------------------------------------------------------------------
    void algorithm(){
        boolean quitter = false;
        ecranTitre();
        do{ //boucle pour revenir au premier choix
            int premierChoix = premierChoix();

            //LE JEU
            if(premierChoix == 1){
                int nbJoueurs = -1;
                int tempsQuestions = -1;

                while(rejouer == 3 || rejouer == 2){
                    //phase de choix des joueurs
                    nbJoueurs = nbJoueurs();
                    println("Cette partie aura " + nbJoueurs + " joueurs.");
                    ensembleJoueur = new Joueur[nbJoueurs];
                    remplirTab();

                    //phase de connection des joueurs
                    for(int i = 1; i<=length(ensembleJoueur);i++){
                        ligne();
                        box("Connection joueur " + i + " :");
                        int compteLigne = connection();
                        ensembleJoueur[i-1] = newJoueur(i,
                                            charAt(ensembleSkins,StringToInt(getCell(comptes,compteLigne,7))),
                                            getCell(comptes,compteLigne,0),
                                            rAssociéeAuJoueur(i),
                                            vAssociéeAuJoueur(i),
                                            bAssociéeAuJoueur(i),
                                            posX(i));
                        ligne();
                        box("Joueur " + i + " connecté à " + ensembleJoueur[i-1].nom +  " avec succès !");
                    }
                    if(rejouer != 2){
                        //phrase de coeff de temps (difficulté)
                        tempsQuestions = tempsQuestions();
                        box("Vous avez mis le temps au mode " + tempsQuestions + ".");
                        contenuChaqueCase = creerTabContenu(5,11);
                        contenuChaqueCase = placerJoueurs(contenuChaqueCase);
                    }


                    //le jeu ihi (trop bien)
                    leJeu(tempsQuestions);
                }

                while(rejouer == 1){
                    ensembleJoueur = new Joueur[nbJoueurs];
                    remplirTab();
                    for(int i = 1; i<=length(ensembleJoueur);i++){
                        ensembleJoueur[i-1] = newJoueur(i,ensembleJoueur[i-1].skin,ensembleJoueur[i-1].nom,
                                            rAssociéeAuJoueur(i),
                                            vAssociéeAuJoueur(i),
                                            bAssociéeAuJoueur(i),
                                            posX(i));
                    }
                    
                    leJeu(tempsQuestions);
                }

                if(rejouer == 5){
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
        println("4 : quitter");
        ligne();
        String saisie;
        do{
            print("Choisissez ce que vous voulez faire : ");
            saisie = readString();
            if(saisie == "" || !(controleSaisieInt(saisie,'4'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                ligne();
                saisie = "9";  
            }
        }while(!(controleSaisieInt(saisie, '4')));

        return StringToInt(saisie);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////__________  /////////////////////////////////////////////////////////////////
//////////////////////////////|GÉNÉRALES|  /////////////////////////////////////////////////////////////////
//////////////////////////////|_________|  /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------générales-----------------------------------------------------------------------------
    boolean controleSaisieInt(String saisie, char max){
        boolean result = true;
        if(length(saisie) > 1 ||
            charAt(saisie, 0) > max ||
            charAt(saisie, 0) < '1'){
            result = false;
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

    int lignesCSV(CSVFile f){
        return rowCount(f);
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
            int i = fin;
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
        if(length(entree) == 1){
            return charAt(entree,0) - '0';
        }
        else if(length(entree) == 2){
            int dizaine = (charAt(entree,0) - '0') * 10;
            return dizaine + (charAt(entree,1) - '0');
        }
        else if(length(entree) == 3){
            int centaine = (charAt(entree,0) - '0') * 100;
            int dizaine = (charAt(entree,1) - '0') * 10;
            return centaine + dizaine + (charAt(entree,2) - '0');
        }else{
            return -1;
        }
    }

    char intToChar(int entree){
        return (char)(entree + '0');
    }

    String intToString(int entree){
        return "" + entree;
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

    Partie newPartie(double coeff, int nbJoueurs, int score){
        Partie p = new Partie();
        p.coeffTemps = coeff;
        p.nbJoueurs = nbJoueurs;
        p.scoreDeVictoire = score;
        return p;
    }

    ContenuCases newContenuCases(String event, String question, int reponse, int temps){
        ContenuCases c = new ContenuCases();
        c.evenement = event;
        c.question = question;
        c.reponse = reponse;
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
    int connection(){
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie, intToChar(rowCount(comptes)-1)))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
            else if(!(pasDejaPris(getCell(comptes,StringToInt(saisie),0)))){
                ligne();
                box("/!\\ Compte déja utilisé /!\\");
            }
        }while(!(controleSaisieInt(saisie, intToChar(rowCount(comptes)-1))) ||
                !(pasDejaPris(getCell(comptes,StringToInt(saisie),0)))); //on est sur que l'entrée est bonne


        if(!(equals(getCell(comptes,StringToInt(saisie),0),"Invité"))){ //Compte classique
            if(!(connectionMDP(StringToInt(saisie)))){
                ligne();
                box("/!\\ MDP incorrect /!\\");
                saisie = intToString(connection());
            }
        }else{ //Si c'est un invité
            do{
                print("Choisissez votre pseudo : ");
                saisie = readString();
                if(equals(saisie, "")){
                    box("/!\\ Saisie incorrecte /!\\");
                }
            }while(equals(saisie,""));
        }
        return StringToInt(saisie); //...est convertie en nom du compte
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

    void remplirTab(){ //remplir tab ensemble joueur pour pv le lire
        for(int i = 0; i < length(ensembleJoueur);i++){
            ensembleJoueur[i] = newJoueur(i+1,' ',"   ",0,0,0,0);
        }
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie,'4'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, '4')));
        return StringToInt(saisie);
    }

    int tempsQuestions(){
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie,'6'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, '6')));
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

    boolean decrypterCasesPossibles(String casesPossibles, String saisie){
        boolean result = false;
        for(int i = 0; i < length(casesPossibles); i++){
            if(charAt(casesPossibles,i) == charAt(saisie, 0)){
                result = true;
            }
        }
        return result;
    }

    int calculTempsQuestions(int coeff, int tempsQuestion){
        return tempsQuestion * coeff + 20;
    }
    
//-------------gameplay-----------------------------------------------------------------------------

    void leJeu(int tempsQuestions){
        while(scoreMax(length(ensembleJoueur)) < 10){
            print(clear);
            ligne();
            afficherTab(contenuChaqueCase);

            int uneCase = tourDeplacement(); //partie deplacement du tour
            if(uneCase != -1){
                tourQuestion(tempsQuestions, uneCase); //partie gameplay du tour
            }
            readString();

            joueurActu = (joueurActu + 1)%length(ensembleJoueur); //change joueur
        }

        tourVictoire();

        rejouer();
    }

    int tourDeplacement(){
        println("Au tour du messager " + ensembleJoueur[joueurActu].nom + " !");
        int uneCase = choixCase();
        if(uneCase != -1){
            box(contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].evenement);
        }
        return uneCase;
    }

    void tourQuestion(int tempsQuestions, int uneCase){
        box("Pressez \"entrée\" pour reveler la question.");
        readString();
        //debut timer
        long debut = getTime();
        box("Question pour le messager " + 
            ensembleJoueur[joueurActu].nom + 
            " : " + 
            contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].question + 
            " (Vous avez " + 
            calculTempsQuestions(tempsQuestions, contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].temps) + 
            " secondes.)");
        print("Votre réponse : ");
        String reponse = readString();
        if(verifQuestion(contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].reponse,reponse)){
            long fin = getTime();
            float tempsPrisMS = (fin - debut);
            float tempsPris = tempsPrisMS/1000;
            if(tempsQuestions != 6 || calculTempsQuestions(tempsQuestions, contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].temps) < tempsPris){
                ensembleJoueur[joueurActu].score = ensembleJoueur[joueurActu].score + 1;
                updateTab(uneCase);
                box("Vous avez mis " + tempsPris + "s à répondre.");
            }else if(tempsQuestions != 6){
                box("Vous avez mis trop de temps à répondre messager. Soit " + tempsPris + " secondes.");
            }
        }
        long fin = getTime();
        //fin timer
    }

    void tourVictoire(){
        print(clear);
        ligne();
        box("Bien joué ! Le messager " + ensembleJoueur[joueurActu].nom + " a atteint le score de 10 et remporte la partie ! Félicitations !");
    }


    int choixCase(){
        String saisie;
        String casesPossibles = "";
        do{
            String texte = "Choisissez votre prochaine case (";
            for(int i = 0; i < length(contenuChaqueCase,1);i++){
                if(equals(contenuChaqueCase[i][ensembleJoueur[joueurActu].score+1].remplirCase,"   ")){
                    if(i == ensembleJoueur[joueurActu].posX ||
                        i == ensembleJoueur[joueurActu].posX-1 ||
                        i == ensembleJoueur[joueurActu].posX+1){
                        texte = texte + (i+1) + ", ";
                        casesPossibles = casesPossibles + (i+1);
                    }
                }
            }
            texte = texte + ("T, S, U) ou \"I\" si vous êtes perdus : ");
            if(length(casesPossibles) > 0){
                box(texte);
            }else{
                box("Vous n'avez nulle part ou aller messager... Prenez un peu de repos.");
                return -1;
            }
            saisie = readString();
            if(commandeExterne(saisie)){
                //appliquerCommandeExterne(saisie);
                saisie = "9";
            }
            else if(length(saisie) != 1 || !(decrypterCasesPossibles(casesPossibles, saisie))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(decrypterCasesPossibles(casesPossibles, saisie)));
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie,'4'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, '4')));
        int rejouer = StringToInt(saisie);
    }


    int scoreMax(int nbJoueurs){
        int scoreMax = ensembleJoueur[0].score;
        for(int i = 1; i < nbJoueurs;i++){
            if(ensembleJoueur[i].score > scoreMax){
                scoreMax = ensembleJoueur[i].score;
            }
        }
        return scoreMax;
    }
//-------------tableau de jeu-----------------------------------

    ContenuCases[][] creerTabContenu(int lignes, int colonnes, char rien){
        ContenuCases[][] Tcréer = new ContenuCases[lignes][colonnes];
        for(int i = 0; i < lignes; i++){
            for(int j = 0; j < colonnes; j++){
                String zone /*le nom de la zone*/ = getCell(zones, (int)(random()*(lignesCSV(zones)-1))+1, 0);
                String evenementZone;
                int evenementLigne;
                do{ //trouver un evenement possible dans cette zone
                    evenementLigne = StringToInt(getCell(evenements, (int)(random()*(lignesCSV(evenements)-1))+1, 0));
                    evenementZone = getCell(evenements, evenementLigne, 3);
                }while(!(equals(evenementZone, zone))); 
                String descriptionEvenement = getCell(evenements, evenementLigne, 2);
                String evenement /*le nom*/ = getCell(evenements, evenementLigne, 1);
                String questionEvenement;
                int questionLigne;

                do{ //trouver une question liée a cet evenement
                    questionLigne = StringToInt(getCell(questions, (int)(random()*(lignesCSV(questions)-1))+1, 0));
                    questionEvenement = getCell(questions, questionLigne, 4);
                }while(!(equals(questionEvenement, evenement)));  
                Tcréer[i][j] = newContenuCases(descriptionEvenement, 
                                getCell(questions, questionLigne, 2), 
                                StringToInt(getCell(questions, questionLigne, 3)),
                                StringToInt(getCell(questions, questionLigne, 5)));
            }
        }
        return Tcréer;
    }

    ContenuCases[][] creerTabContenu(int lignes, int colonnes){
        ContenuCases[][] Tcreer = new ContenuCases[lignes][colonnes];
        for(int i = 0; i < lignes; i++){
            for(int j = 0; j < colonnes; j++){

                // Tirage de la zone
                int ligneZone = (int)(random() * (lignesCSV(zones) - 1)) + 1;
                String zone = getCell(zones, ligneZone, 0);

                // Tirage d’un événement COMPATIBLE avec la zone
                int ligneEvenement;
                String zoneEvenement;
                do{
                    ligneEvenement = (int)(random() * (lignesCSV(evenements) - 1)) + 1;
                    zoneEvenement = getCell(evenements, ligneEvenement, 3);
                }while(!equals(zoneEvenement, zone));

                String descriptionEvenement = getCell(evenements, ligneEvenement, 2);
                String nomEvenement = getCell(evenements, ligneEvenement, 1);

                // Tirage d’une question COMPATIBLE avec l’événement
                int ligneQuestion;
                String evenementQuestion;
                do{
                    ligneQuestion = (int)(random() * (lignesCSV(questions) - 1)) + 1;
                    evenementQuestion = getCell(questions, ligneQuestion, 4);
                }while(!equals(evenementQuestion, nomEvenement));

                // Création du contenu
                Tcreer[i][j] = newContenuCases(
                    descriptionEvenement,
                    getCell(questions, ligneQuestion, 2),
                    StringToInt(getCell(questions, ligneQuestion, 3)),
                    StringToInt(getCell(questions, ligneQuestion, 5))
                );
            }
        }
        return Tcreer;
    }

    ContenuCases[][] placerJoueurs(ContenuCases[][] tab2){
        ContenuCases tab[][] = tab2;
        if(length(ensembleJoueur) == 1){
            tab[2][0].remplirCase = rgb(255,0,0,false) + " " + ensembleJoueur[joueurActu].skin + " " + RESET;
        }
        if(length(ensembleJoueur) == 2){
            tab[1][0].remplirCase = rgb(255,0,0,false) + " " + ensembleJoueur[joueurActu].skin + " " + RESET;
            tab[3][0].remplirCase = rgb(0,0,255,false) + " " + ensembleJoueur[joueurActu+1].skin + " " + RESET;
        }
        if(length(ensembleJoueur) == 3){
            tab[0][0].remplirCase = rgb(255,0,0,false) + " " + ensembleJoueur[joueurActu].skin + " " + RESET;
            tab[2][0].remplirCase = rgb(0,0,255,false) + " " + ensembleJoueur[joueurActu+1].skin + " " + RESET;
            tab[4][0].remplirCase = rgb(0,255,0,false) + " " + ensembleJoueur[joueurActu+2].skin + " " + RESET;;
        }
        if(length(ensembleJoueur) == 4){
            tab[0][0].remplirCase = rgb(255,0,0,false) + " " + ensembleJoueur[joueurActu].skin + " " + RESET;
            tab[1][0].remplirCase = rgb(0,0,255,false) + " " + ensembleJoueur[joueurActu+1].skin + " " + RESET;          
            tab[2][0].remplirCase = rgb(0,255,0,false) + " " + ensembleJoueur[joueurActu+2].skin + " " + RESET;
            tab[3][0].remplirCase = rgb(255,255,255,false) + " " + ensembleJoueur[joueurActu+3].skin + " " + RESET;
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
        contenuChaqueCase[uneCase][ensembleJoueur[joueurActu].score].remplirCase = rgb(ensembleJoueur[joueurActu].r,
                                                        ensembleJoueur[joueurActu].v,
                                                        ensembleJoueur[joueurActu].b,false) 
                                                        + " " + ensembleJoueur[joueurActu].skin + " " + RESET; 
        contenuChaqueCase[ensembleJoueur[joueurActu].posX][ensembleJoueur[joueurActu].score-1].remplirCase = "   ";
        ensembleJoueur[joueurActu].posX = uneCase;
    }
//-------------autre-----------------------------------

    boolean verifQuestion(int réponse, String saisie){
        if(réponse == StringToInt(saisie)){
            if(ensembleJoueur[joueurActu].score != 9){
                println("Bien joué messager " + ensembleJoueur[joueurActu].nom + ", tu peux continuer ton chemin !");
            }
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
        String saisie; //chiffre du compte... (voir derniere ligne fonction)
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie, intToChar(rowCount(comptes))))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "99";
            }
        }while(!(controleSaisieInt(saisie, intToChar(rowCount(comptes))))); //on est sur que l'entrée est bonne

        //acces au compte
        if(StringToInt(saisie) < rowCount(comptes)-1){
            if(!(connectionMDP(StringToInt(saisie)))){
                ligne();
                box("/!\\ MDP incorrect /!\\");
                listeCompte();
            }else{
                choixVerifCompte(saisie);
            }
        }

        //creer un compte
        else if(StringToInt(saisie) == rowCount(comptes)-1){
            
            String[][] nouveauCompte = new String[rowCount(comptes) + 1][columnCount(comptes)];
            for(int i = 0; i < rowCount(comptes)-1; i++){
                for(int j = 0; j < columnCount(comptes);j++){
                    nouveauCompte[i][j] = getCell(comptes,i,j);
                }
            }
            do{
                print("Choisissez le nom de votre compte : ");
                nouveauCompte[length(nouveauCompte,1)-2][0] = readString();
                if(length(nouveauCompte[length(nouveauCompte,1)-2][0]) < 2){
                    println("Nom trop court. Recommencez. (2 car min)");
                    readString();
                }else{
                    print("Choisissez le MDP de votre compte : ");
                    nouveauCompte[length(nouveauCompte,1)-2][1] = readString();
                    if(length(nouveauCompte[length(nouveauCompte,1)-2][1]) < 3){
                        println("MDP trop court. Recommencez. (3 car min)");
                        readString();
                    }
                }
            }while(length(nouveauCompte[length(nouveauCompte,1)-2][0]) < 2 &&
                    length(nouveauCompte[length(nouveauCompte,1)-2][1]) < 3);

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
            box("Nouveau compte crée avec succès !");
            readString();
            listeCompte();
        }

        print(clear);
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
            if(equals(saisie,"") || !(controleSaisieInt(saisie,'6'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                saisie = "9";
            }
        }while(!(controleSaisieInt(saisie, '6')));

        if(StringToInt(saisie) == 1){ //voir stats
            float victoires = StringToInt(getCell(comptes, StringToInt(saisie), 0));
            float defaites = StringToInt(getCell(comptes, StringToInt(saisie), 4));
            float wr = victoires/defaites;
            box("Vous avez gagné " + getCell(comptes, StringToInt(saisie), 4) +
                " fois et perdu " + getCell(comptes, StringToInt(saisie), 5) + " fois. " +
                "Pour un total de " + wr + "% de taux de victoires.");
            box("De plus, vous êtes niveau " + 
                getCell(comptes, StringToInt(saisie), 2) + 
                " avec " + 
                getCell(comptes, StringToInt(saisie), 3) + 
                "XP.");
            print("Entrez pour continuer.");
            readString();
            print(clear);
            choixVerifCompte(compte);

        }

        if(StringToInt(saisie) == 2){ //changer nom
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
            }while(length(nouveauNom) < 2);

            nouveauNom = readString();
            lesNoms[StringToInt(compte)][0] = nouveauNom;
            saveCSV(lesNoms, "CSV/comptes.CSV");
            box("Nom changé avec succès !");
            readString();
            print(clear);
            choixVerifCompte(compte);
        }

        if(StringToInt(saisie) == 3){ //changer mdp
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
                    box("Mot de passe changé avec succès !");
                }else{
                    box("Les mots de passe ne correspondent pas.");
                }
            }while(!(equals(nouveauMDP, ConfirmezMDP)));
            readString();
            print(clear);
            choixVerifCompte(compte);
        }

        if(StringToInt(saisie) == 4){ //skin
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
            lesSkins[StringToInt(compte)][7] = choisirSkin;
            saveCSV(lesSkins, "CSV/comptes.CSV");
            box("Skin changé avec succès !");
            readString();
            print(clear);
            choixVerifCompte(compte);
        }

        if(StringToInt(saisie) == 5){ //retour
            listeCompte();
        }

        if(StringToInt(saisie) == 6){ //delete
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
                    print("A");
                    lesComptes[i][0] = getCell(comptes,i+1,0);
                    lesComptes[i][1] = getCell(comptes,i+1,1);
                    lesComptes[i][2] = getCell(comptes,i+1,2);
                    lesComptes[i][3] = getCell(comptes,i+1,3);
                    lesComptes[i][4] = getCell(comptes,i+1,4);
                    lesComptes[i][5] = getCell(comptes,i+1,5);
                }

                saveCSV(lesComptes, "CSV/comptes.CSV");
                box("Compte supprimé avec succès.");
            }else{
                box("Suppression annulée.");
            }
            readString();
            print(clear);
            listeCompte();
        }
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
        assertTrue(controleSaisieInt("2", '3'));
        assertFalse(controleSaisieInt("4",'3'));
        assertFalse(controleSaisieInt("-1",'3'));
        assertFalse(controleSaisieInt("0",'3'));
        assertFalse(controleSaisieInt("abcd",'3'));

    }

    void test_StringToInt(){
        assertEquals(StringToInt("120"),120);
        assertEquals(StringToInt("9"),9);
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
        p.coeffTemps = 1.0;
        p.nbJoueurs = 3;
        p.scoreDeVictoire = 10;
        assertEquals(newPartie(1.0,3,10).coeffTemps,p.coeffTemps);
        assertEquals(newPartie(1.0,3,10).nbJoueurs,p.nbJoueurs);
        assertEquals(newPartie(1.0,3,10).scoreDeVictoire,p.scoreDeVictoire);
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
//-------------jouer-----------------------------------------------------------------------------


//-------------connection-----------------------------------------------------------------------------


//-------------règles-----------------------------------------------------------------------------


}