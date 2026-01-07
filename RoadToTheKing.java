/* 
TODO : 
- faire la mecha de temps (avec un coeff bien défini)
- rédiger et implémenter les règles
- implémenter la fonction de comptes
- changer la verif des cases dispo
- implémenter la fonction d'objets (simple)
- implémenter les boss ?
- avoir pour chaque joueur une pos x et y. Pour pv enlever leur trace.
*/

import extensions.File;
import extensions.CSVFile;
class RoadToTheKing extends Program{
//variables globales
final String clear = "\033[H\033[2j";
final CSVFile comptes = loadCSV("CSV/comptes.CSV");
final CSVFile dilemme = loadCSV("CSV/dilemmes.CSV",'_');
final CSVFile evenements = loadCSV("CSV/evenements.CSV",'_');
final CSVFile items = loadCSV("CSV/items.CSV",'_');
final CSVFile questions = loadCSV("CSV/questions.CSV",'_');
final CSVFile zones = loadCSV("CSV/zones.CSV",'_');
final String caseAffichage = "   ";
Joueur[] ensembleJoueur; //trouver joueur actu et load les données dans les bonnes cases
ContenuCases[][] contenuChaqueCase; 
String[][] tableauDeJeu;
Partie partie;
int joueurActu = 0; //joueur actuel

//-------------programme-----------------------------------------------------------------------------
    void algorithm(){
        ecranTitre();
        int premierChoix = premierChoix();

        //LE JEU
        if(premierChoix == 1){
            int nbJoueurs = nbJoueurs();
            println("Cette partie aura " + nbJoueurs + " joueurs.");
            ensembleJoueur = new Joueur[nbJoueurs];
            remplirTab();
            for(int i = 1; i<=length(ensembleJoueur);i++){
                ligne();
                box("Connection joueur " + i + " :");
                ensembleJoueur[i-1] = newJoueur(i,connection(),
                                      rAssociéeAuJoueur(i),
                                      vAssociéeAuJoueur(i),
                                      bAssociéeAuJoueur(i),
                                      posX(i));
                ligne();
                box("Joueur " + i + " connecté à " + ensembleJoueur[i-1].nom +  " avec succès !");
            }
            int tempsQuestions = tempsQuestions();
            box("Vous avez mis le temps au mode " + tempsQuestions + ".");
            contenuChaqueCase = creerTabContenu(5,10);
            tableauDeJeu = creerTab(5,10);

            while(scoreMax(length(ensembleJoueur)) < 10){
                ligne();
                afficherTab(tableauDeJeu);

                println("Au tour du messager " + ensembleJoueur[joueurActu].nom + " !");
                int uneCase = choixCase();
                box(contenuChaqueCase[ensembleJoueur[joueurActu].score][uneCase].evenement);

                box("Pressez \"entrée\" pour reveler la question.");
                readString();
                //debut timer
                long debut = getTime();
                box("Question pour le messager " + 
                    ensembleJoueur[joueurActu].nom + 
                    " : " + 
                    contenuChaqueCase[ensembleJoueur[joueurActu].score][uneCase].question + 
                    "Vous avez " + 
                    calculTempsQuestions(tempsQuestions, contenuChaqueCase[ensembleJoueur[joueurActu].score][uneCase].temps) + 
                    "secondes.");
                print("Votre réponse : ");
                String reponse = readString();
                println("1");
                if(verifQuestion(contenuChaqueCase[ensembleJoueur[joueurActu].score][uneCase].reponse,reponse)){
                    long fin = getTime();
                    double tempsPris = (fin - debut)/1000;
                    if(tempsQuestions != 6 || calculTempsQuestions(tempsQuestions, contenuChaqueCase[ensembleJoueur[joueurActu].score][uneCase].temps) < tempsPris){
                        ensembleJoueur[joueurActu].score = ensembleJoueur[joueurActu].score + 1;
                        updateTab(uneCase);
                        box("Vous avez mis " + tempsPris + "s à répondre.");
                    }else{
                        box("Vous avez mis trop de temps à répondre messager. Soit " + tempsPris + " secondes.");
                    }
                }
                long fin = getTime();
                //fin timer
                joueurActu = (joueurActu + 1)%length(ensembleJoueur);

            }
            ligne();
            ligne();
            println("Bien joué ! Le messager " + ensembleJoueur[joueurActu].nom + " a atteint le score de 10 et remporte la partie ! Félicitations !");
        }

        //LES COMPTES

        //LES REGLES
        /* if(premierChoix == 3){
            File règles = newFile("txt/règles.txt");
            while(ready(règles)){
                println(readLine(règles));
            }*
        } */
        
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////____  /////////////////////////////////////////////////////////////////
//////////////////////////////|GRUB| /////////////////////////////////////////////////////////////////
//////////////////////////////|____| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------ecran titre-----------------------------------------------------------------------------
    void ecranTitre(){
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
    }
//-------------premier choix-----------------------------------------------------------------------------

    int premierChoix(){
        int result;
        println("1 : Jouer");
        println("2 : Vos comptes");
        println("3 : Règles");
        ligne();
        String saisie;
        do{
            print("Choisissez ce que vous voulez faire : ");
            saisie = readString();
            if(saisie == "" || !(controleSaisieInt(saisie,'3'))){
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
                ligne();
                saisie = "9";  
            }
        }while(!(controleSaisieInt(saisie, '3')));

        if(charAt(saisie,0) == '1'){
            result = 1;
        }
        else if(charAt(saisie,0) == '2'){
            result = 2;
            println("WIP");
            premierChoix();
        }else{
            result = 3;
            println("WIP");
            premierChoix();
        }
        return result;
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
            int dizaine = charAt(entree,0) * 10;
            return dizaine + charAt(entree,1) - '0';
        }
        else if(length(entree) == 3){
            int centaine = charAt(entree,0) * 100;
            int dizaine = charAt(entree,1) * 10;
            return centaine + dizaine + charAt(entree,2) - '0';
        }else{
            return -1;
        }
    }

    char intToChar(int entree){
        return (char)(entree + '0');
    }
    
//-------------creation types-----------------------------------------------------------------------------
    Joueur newJoueur(int jno, String nom, int r, int v, int b, int posX){
        Joueur j = new Joueur();
        j.jno = jno;
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
    String connection(){
        String saisie;
        do{

            ligne();
            println("Les différents comptes :");
            for(int i = 1; i < rowCount(comptes);i++){
                if(pasDejaPris(getCell(comptes,i,0))){
                    println(i + " : " + getCell(comptes, i, 0));
                }
            }
            print("Votre choix : ");
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
                !(pasDejaPris(getCell(comptes,StringToInt(saisie),0))));


        if(!(equals(getCell(comptes,StringToInt(saisie),0),"Invité"))){
            if(connectionMDP(StringToInt(saisie))){
                //pass
            }else{
                ligne();
                box("/!\\ MDP incorrect /!\\");
                saisie = connection();
            }
            return getCell(comptes, StringToInt(saisie), 0);
        }else{
            print("Choisissez votre pseudo : ");
            return readString();
        }
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
            ensembleJoueur[i] = newJoueur(i+1,caseAffichage,0,0,0,0);
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

    int choixCase(){
        String saisie;
        String casesPossibles = "";
        do{
            print("Choisissez votre prochaine case (");
            for(int i = 0; i < length(tableauDeJeu,1);i++){
                if(equals(tableauDeJeu[i][ensembleJoueur[joueurActu].score+1],caseAffichage)){
                    print(i+1 + " ou ");
                    casesPossibles = "" + (i+1) + ",";
                }
            }
            print("commandes externes) : ");
            saisie = readString();
            if(decrypterCasesPossibles(casesPossibles, saisie)){
                    //pass
            }else{
                ligne();
                box("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(decrypterCasesPossibles(casesPossibles, saisie)));
        return StringToInt(saisie);
    }

    boolean decrypterCasesPossibles(String casesPossibles, String saisie){
        for(int i = 0; i < length(saisie); i++){
            if(charAt(saisie,i) > '9' || charAt(saisie,i) < '1'){
                return false;
            }
        }
        return true;
    } //a refaire. Pas accurate

    int calculTempsQuestions(int coeff, int tempsQuestion){
        return tempsQuestion * (coeff * 2);
    }
    
//-------------gameplay-----------------------------------------------------------------------------
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
    String[][] creerTab(int lignes, int colonnes){
        String [][] Tcréer = new String[lignes][colonnes];
        Tcréer = remplirTab(Tcréer);
        placerJoueurs(Tcréer); 
        return Tcréer;
    }

    ContenuCases[][] creerTabContenu(int lignes, int colonnes){
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

    String[][] remplirTab(String[][] tab){
        for(int i = 0; i<length(tab,1);i++){
            for(int j = 0; j<length(tab,2);j++){
                tab[i][j] = caseAffichage;
            }
        }
        return tab;
    }

    void placerJoueurs(String[][] tab){
        if(length(ensembleJoueur) == 1){
            tab[2][0] = rgb(255,0,0,false) + caseAffichage + RESET;
        }
        if(length(ensembleJoueur) == 2){
            tab[1][0] = rgb(255,0,0,false) + caseAffichage + RESET;
            tab[3][0] = rgb(0,0,255,false) + caseAffichage + RESET;
        }
        if(length(ensembleJoueur) == 3){
            tab[0][0] = rgb(255,0,0,false) + caseAffichage + RESET;
            tab[2][0] = rgb(0,0,255,false) + caseAffichage + RESET;
            tab[4][0] = rgb(0,255,0,false) + caseAffichage + RESET;;
        }
        if(length(ensembleJoueur) == 4){
            tab[0][0] = rgb(255,0,0,false) + caseAffichage + RESET;
            tab[1][0] = rgb(0,0,255,false) + caseAffichage + RESET;          
            tab[2][0] = rgb(0,255,0,false) + caseAffichage + RESET;
            tab[3][0] = rgb(255,255,255,false) + caseAffichage + RESET;
        }
    }

    void dessineBordCase (int m){
        print("+");
        for(int j =0;j<m;j++){
            print("---");
            print('+');
        }
        println("");
    }

    void dessineContenuCase(int m, int ligne, String[][] tab){
        print('|');
        for(int col=0; col<m; col++){
            print(tab[ligne][col]);
            print('|');
        }
        println("");
    }

    void afficherTab(String[][] t) {
        for(int i=0;i < length(t,1);i++){
            dessineBordCase(length(t,2));
            dessineContenuCase(length(t,2),i,t);
        }
        dessineBordCase(length(t,2));
    }

    void updateTab(int uneCase){
        tableauDeJeu[uneCase-1][ensembleJoueur[joueurActu].score] = rgb(ensembleJoueur[joueurActu].r,
                                                        ensembleJoueur[joueurActu].v,
                                                        ensembleJoueur[joueurActu].b,false) 
                                                        + caseAffichage + RESET; 
        tableauDeJeu[ensembleJoueur[joueurActu].posX][ensembleJoueur[joueurActu].score-1] = caseAffichage;
        ensembleJoueur[joueurActu].posX = uneCase-1;
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
///////////////////////////////_________  /////////////////////////////////////////////////////////////////
//////////////////////////////|connexion| /////////////////////////////////////////////////////////////////
//////////////////////////////|_________| /////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////



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
        assertEquals(StringToInt("0ezfesgz"),0);
        assertEquals(StringToInt("9"),9);
        assertEquals(StringToInt("161894"),1);
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
        assertEquals(newJoueur(3,"test",0,0,0,0).jno,j.jno);
        assertEquals(newJoueur(3,"test",0,0,0,0).nom,j.nom);
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
//-------------jouer-----------------------------------------------------------------------------


//-------------connection-----------------------------------------------------------------------------


//-------------règles-----------------------------------------------------------------------------


}