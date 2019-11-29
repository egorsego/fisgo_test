package application_manager.api_manager.goods;

public class GoodsQueryBuilder {

    /**
     * Метод возвращает запрос на добавление товара в БД(goodsDb.db) в таблицу GOODS
     *
     * @param good - товар
     * @return запрос
     */
    public static String getQueryInsertGood(Goods good) {
        return "echo \"attach '/FisGo/goodsDb.db' as goods; insert into goods.GOODS " +
                "(ID, PRECISION, MEASURE, NAME, BARCODE, GOODS_TYPE, PRICE, NDS, ATTRIBUTES)" +
                " values (" +
                good.getID() + ", " +
                good.getPRECISION() + ", " +
                "'" + good.getMEASURE() + "', " +
                "'" + good.getNAME() + "', " +
                "'" + good.getBARCODE() + "', " +
                good.getGOODS_TYPE() + ", " +
                good.getPRICE() + ", " +
                good.getNDS() + ", " +
                good.getATTRIBUTES() +
                ");\" " +
                "| sqlite3 /FisGo/goodsDb.db";
    }

    public static String getQueryInsertGoodCode(GoodsCode goodsCode) {
        return "echo \"attach '/FisGo/goodsDb.db' as goods;" +
                " insert into goods.GOODS_CODE (GOODS_ID, HASH_VAL, TYPE) values (" +
                goodsCode.getGOODS_ID() + ", " +
                goodsCode.getHASH_VAL() + ", " +
                goodsCode.getTYPE() + ");\" " +
                "| sqlite3 /FisGo/goodsDb.db";
    }

    public static String getDeleteGoodsTable() {
        return "echo \"attach '/FisGo/goodsDb.db' as GOODS; delete from goods.GOODS;\" " +
                "| sqlite3 /FisGo/goodsDb.db";
    }

    public static String getDeleteGoodsCodeTable() {
        return "echo \"attach '/FisGo/goodsDb.db' as GOODS_CODE; delete from goods_code.GOODS_CODE;\" " +
                "| sqlite3 /FisGo/goodsDb.db";
    }

}
