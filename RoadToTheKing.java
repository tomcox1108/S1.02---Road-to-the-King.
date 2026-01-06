/* 
TODO : 
- regler le dernier bug de connection (rater mdp compte x et reussir compte y -> connecté compte x)
- faire la mecha de temps (avec un coeff bien défini)
- commencer le jeu en lui meme
- rédiger et implémenter les règles
- implémenter la fonction de comptes
*/


import extensions.File;
import extensions.CSVFile;
class RoadToTheKing extends Program{
//variables globales
CSVFile comptes = loadCSV("CSV/comptes.CSV");
CSVFile dilemme = loadCSV("CSV/dilemmes.CSV",'_');
CSVFile evenements = loadCSV("CSV/evenements.CSV",'_');
CSVFile items = loadCSV("CSV/items.CSV",'_');
CSVFile questions = loadCSV("CSV/questions.CSV",'_');
CSVFile zones = loadCSV("CSV/zones.CSV",'_');
Joueur[] ensembleJoueur;
Partie partie;
int nbJoueurs;
int joueur = 0; //joueur actuel

//-------------programme-----------------------------------------------------------------------------
    void algorithm(){
        ecranTitre();
        int premierChoix = premierChoix();

        //LE JEU
        if(premierChoix == 1){
            nbJoueurs = nbJoueurs();
            println("Cette partie aura " + nbJoueurs + " joueurs.");
            ensembleJoueur = new Joueur[nbJoueurs];
            remplirTab();
            for(int i = 1; i<=nbJoueurs;i++){
                ligne();
                println("Connection joueur " + i + " :");
                ensembleJoueur[i-1] = newJoueur(i,connection());
                
                ligne();
                println("Joueur " + i + " connecté à " + ensembleJoueur[i-1].nom +  " avec succès !");
            }
            int tempsQuestions = tempsQuestions();
            println("Vous avez mis le temps au mode " + tempsQuestions + ".");
            while(scoreMax(nbJoueurs) < 10){
                joueur = (joueur + 1)%nbJoueurs;
                ligne();

                println("Au tour du messager " + ensembleJoueur[joueur].nom + " !");
                String zone /*le nom*/ = getCell(zones, (int)(random()*(lignesCSV(zones)-1))+1, 0);
            
                String evenementZone;
                int evenementLigne;
                do{
                    evenementLigne = StringToInt(getCell(evenements, (int)(random()*(lignesCSV(evenements)-1))+1, 0));
                    evenementZone = getCell(evenements, evenementLigne, 3);
                }while(!(equals(evenementZone, zone)));  
                String descriptionEvenement = getCell(evenements, evenementLigne, 2);
                String evenement /*le nom*/ = getCell(evenements, evenementLigne, 1);
                println(descriptionEvenement);

                String questionEvenement;
                println("1");
                int questionLigne;
                println("2");

                do{
                    println("3");
                    questionLigne = StringToInt(getCell(questions, (int)(random()*(lignesCSV(questions)-1))+1, 0));
                    println("4 : " + questionLigne);
                    questionEvenement = getCell(questions, questionLigne, 4);
                    println("5 : " + questionEvenement + " | " + evenement);
                }while(!(equals(questionEvenement, evenement)));  
                
                String question = getCell(questions, questionLigne, 2);
                print("Pressez \"entrée\" pour reveler la question.");
                readString();
                println("Question pour le messager " + ensembleJoueur[joueur].nom + " : " + question);
                print("Votre réponse : ");
                String reponse = readString();
                if(verifQuestion(reponse,questionLigne,questions)){
                    ensembleJoueur[joueur].score = ensembleJoueur[joueur].score + 1;
                }

            }
            ligne();
            ligne();
            println("Bien joué ! Le messager " + ensembleJoueur[joueur].nom + " a atteint le score de 10 et remporte la partie ! Félicitations !");
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
        String saisie;
        do{
            print("Choisissez ce que vous voulez faire : ");
            saisie = readString();
            if(controleSaisieInt(saisie,'3')){
                //pass
            }else{
                println("/!\\ Saisie incorrecte /!\\");
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

    int lignesCSV(CSVFile f){
        return rowCount(f);
    }
//-------------convertions-----------------------------------------------------------------------------
    int StringToInt(String entree){
        char result = charAt(entree,0);
        int trueResult = result - '0';
        return trueResult;
    }

    char intToChar(int entree){
        return (char)(entree + '0');
    }
    
//-------------creation types-----------------------------------------------------------------------------
    Joueur newJoueur(int jno, String nom){
        Joueur j = new Joueur();
        j.jno = jno;
        j.nom = nom;
        return j;
    }

    Partie newPartie(double coeff, int nbJoueurs, int score){
        Partie p = new Partie();
        p.coeffTemps = coeff;
        p.nbJoueurs = nbJoueurs;
        p.scoreDeVictoire = score;
        return p;
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
            if((controleSaisieInt(saisie, intToChar(rowCount(comptes)-1)))){
                if(!(pasDejaPris(getCell(comptes,StringToInt(saisie),0)))){
                    ligne();
                    println("/!\\ Compte déja utilisé /!\\");
                }
                //pass
            }
            else{
                ligne();
                println("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(controleSaisieInt(saisie, intToChar(rowCount(comptes)-1))) ||
                !(pasDejaPris(getCell(comptes,StringToInt(saisie),0))));



        if(!(equals(getCell(comptes,StringToInt(saisie),0),"Invité"))){
            if(connectionMDP(StringToInt(saisie))){
                //pass
            }else{
                ligne();
                println("/!\\ MDP incorrect /!\\");
                connection();
            }
        }
        return getCell(comptes, StringToInt(saisie), 0);
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
            ensembleJoueur[i] = newJoueur(i+1,"vide");
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
            if(controleSaisieInt(saisie,'4')){
                //pass
            }else{
                ligne();
                println("/!\\ Saisie incorrecte /!\\");
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
            if(controleSaisieInt(saisie,'6')){
                //pass
            }else{
                ligne();
                println("/!\\ Saisie incorrecte /!\\");
            }
        }while(!(controleSaisieInt(saisie, '6')));
        return StringToInt(saisie);
    }


//-------------gameplay-----------------------------------------------------------------------------
    int scoreMax(int nbJoueurs){
        int scoreMax = ensembleJoueur[0].score;
        for(int i = 1; i < length(ensembleJoueur);i++){
            if(ensembleJoueur[i].score > scoreMax){
                scoreMax = ensembleJoueur[i].score;
            }
        }
        return scoreMax;
    }

    /* int[][] creerTab(int lignes, int colonnes){
        int [][] Tcréer = new int[lignes][colonnes];
        placerJoueur(nbJoueurs); 
        return Tcréer;
    } */

    void dessineContenuCase(int m){
        print('|');
        for(int j=0;j<m;j++){
            print("   ");
            print('|');
        }
        println("");
    }

    void dessineBordCase (int m){
        print("+");
        for(int j =0;j<m;j++){
            print("---");
            print('+');
        }
        println("");
    }

    void afficherTab(int[][] t) {
        for(int i=0;i < length(t,1);i++){
            dessineBordCase(length(t,2));
            dessineContenuCase(length(t,2));
        }
        dessineBordCase(length(t,2));
    }

    boolean verifQuestion(String saisie, int n, CSVFile questions){
    if(equals(getCell(questions, n, 3),saisie)){
        println("Bien joué messager " + ensembleJoueur[joueur].nom + ", tu peux continuer ton chemin !");
        return true;
    }else{
        println("Je suis désolé messager " + ensembleJoueur[joueur].nom + ", tu vas devoir rester un moment dans cet endroit.");
        return false;
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////_________  /////////////////////////////////////////////////////////////////
//////////////////////////////|conection| /////////////////////////////////////////////////////////////////
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
        assertEquals(newJoueur(3,"test").jno,j.jno);
        assertEquals(newJoueur(3,"test").nom,j.nom);
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