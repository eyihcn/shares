package eyihcn.dao;


public interface DerivedDayLineFromSouHuRepository {
	
	boolean checkExistsByDate(String date);

	boolean checkExistsByDateAndSharesCode(String date, String sharesCode);
}
