
enum Side {
    BLACK, WHITE;

    public static Side intToSide(int side){
        if (side == 1){
            return WHITE;
        }
        return BLACK;
    }
}



