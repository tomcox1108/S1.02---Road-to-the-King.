class Joueur{
    int jno;
    char skin;
    String nom;
    int score = 0;
    boolean protégé = false; //boite a luciole
    boolean protégéBoiteALuciolePrisEnCompte = false; //pour que clearBoiteALuciole lance 1 tour
    boolean protégéMiquella = false; //benediction de miquella
    int protégéMiquellaPrisEnCompte = 0; //pour que clearBoiteALuciole lance 2 tour
    boolean protégéMineraisDeFer = false;
    int vitesse = 1;
    boolean vista = false;
    boolean secondeVie = false;
    boolean visible = true;
    boolean bloqué = false;
    boolean moaiPlacé = false;
    String tools = "0111111111110";
    boolean confus = false;
    int r;
    int v;
    int b; //couleurs du joueur
    int posX; //la posY est le score
}