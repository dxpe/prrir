import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
public class Symulator {
    static int PRZEJAZD=2; // taksowka w trakcie przejazdu
    int ilosc_taksowek;
    int ilosc_taksowek_koniec;
    int ilosc_klientow;
    int zakonczonych_przejazdow; // ilosc zamowien ktore sie powiodla
    int liczba_kilometrow;
    int dlugosc_zmiany;
    List<Integer> km_arr;
    List<Integer> klient_arr;
    Symulator(int ilosc_taksowek){
        this.ilosc_taksowek = ilosc_taksowek;
        this.ilosc_taksowek_koniec = ilosc_taksowek;
        this.ilosc_klientow = 0;
        this.zakonczonych_przejazdow = 0;
        this.liczba_kilometrow = 0;
        this.dlugosc_zmiany = 0;
        this.km_arr = new ArrayList<>();
        this.klient_arr = new ArrayList<>();
    }
    synchronized int start(int numer){
        ilosc_klientow++;
        System.out.println("#" + numer + " Znaleziono klienta");
        return PRZEJAZD;
    }
    synchronized void koniec(int kilometrow, int dzisiejszych_klinetow, int dlugosc_zmiany_taxi){
        zakonczonych_przejazdow += dzisiejszych_klinetow;
        liczba_kilometrow += kilometrow;
        dlugosc_zmiany += dlugosc_zmiany_taxi;
        km_arr.add(kilometrow);
        klient_arr.add(dzisiejszych_klinetow);
    }
    synchronized void zmniejsz(){
        ilosc_taksowek_koniec--;
        System.out.println("### zabito taxi ###");
    }

    void stats() {
        OptionalDouble average_km = km_arr
                .stream()
                .mapToDouble(a -> a)
                .average();
        OptionalDouble average_klientow = klient_arr
                .stream()
                .mapToDouble(a -> a)
                .average();
        System.out.println("///////////// STATYSTYKI SYMULACJI /////////////");
        System.out.println("Ilosc taksowek: " + ilosc_taksowek + "/" + ilosc_taksowek_koniec + " (start/koniec)");
        System.out.println("Zepsutych taksowek: " + (ilosc_taksowek - ilosc_taksowek_koniec));
        System.out.println("Liczba klientow: " + ilosc_klientow + "/" + zakonczonych_przejazdow + " (liczba klientow ogolem/ilosc udanych przejazdow)");
        System.out.println("Liczba niezadowolonych klientow: " + (ilosc_klientow - zakonczonych_przejazdow));
        System.out.println("Kilometrow ogolem: " + liczba_kilometrow);
        System.out.println("Srednia ilosc kilometrow: " + average_km.getAsDouble());
        System.out.println("Srednia ilosc klientow: " + average_klientow.getAsDouble());
        System.out.println("///////////// STATYSTYKI SYMULACJI /////////////");
    }
}

class Glowna {
    static int ilosc_taksowek=1000;
    static Symulator taxi;
    public Glowna(){
    }
    public static void main(String[] args) throws InterruptedException {
        taxi=new Symulator(ilosc_taksowek);
        List<Thread> threadList = new ArrayList<Thread>();
        for(int i=0;i<ilosc_taksowek;i++) {
            Taxi t = new Taxi(i,2000, taxi);
            t.start();
            threadList.add(t);
        }
        for(Thread t : threadList) {
            t.join();
        }
        taxi.stats();
    }
}

class Taxi extends Thread {
    //definicja stanˇw samolotu
    static int BAZA=1; // baza taksowek
    static int PRZEJAZD=2; // taksowka w trakcie przejazdu
    static int CZEKANIE_NA_KLIENTA=3; // taksowka czeka na klienta
    static int PRZERWA=4; // kierowca ma przerwe
    static int BRAK_PALIWA=5; // nie ma paliwa
    static int WYPADEK=6; // zdarzyl sie wypadek
    static int KONIEC=7; // koniec zmiany taxi
    static int MAX_PALIWO = 2000;
    static int TANKUJ = 1000;
    static int REZERWA = 500;
    //zmienne pomocnicze
    int numer;
    int paliwo;
    int kanister;
    int stan;
    int dzisiejszych_klientow;
    int kilometrow_do_przejazdu;
    int dlugosc_zmiany;
    int laczna_ilosc_kilometrow;
    Symulator taxi;
    Random rand;

    public Taxi(int numer, int paliwo, Symulator sym) {
        this.numer = numer;
        this.paliwo = paliwo;
        this.stan = BAZA;
        this.taxi = sym;
        this.dzisiejszych_klientow = 0;
        rand = new Random();
        this.dlugosc_zmiany = rand.nextInt(600)+120; // zmiana od 2 do 10 godzin
    }

    public void run() {
        while (true) {
            if (stan == BAZA) {
                dlugosc_zmiany -= rand.nextInt(30);
                if (dlugosc_zmiany <= 0) {
                    stan = KONIEC;
                    continue;
                }
                paliwo = MAX_PALIWO;
                kanister = 500;
                stan = CZEKANIE_NA_KLIENTA;
                System.out.println("#" + numer + " Taxi w oczekiwaniu na klienta w bazie.");
                if (rand.nextInt(2) == 1) {
                    dzisiejszych_klientow++;
                    System.out.println("#" + numer + " Taxi w trakcie pracy. Dzisiejszych klientów: " + dzisiejszych_klientow);
                    kilometrow_do_przejazdu = rand.nextInt(15);
                    laczna_ilosc_kilometrow += kilometrow_do_przejazdu;
                    stan = taxi.start(numer);
                } else {
                    System.out.println("#" + numer + " Taxi dalej czeka w bazie.");
                }
            } else if (stan == PRZEJAZD) {
                dlugosc_zmiany -= rand.nextInt(30);
                if (rand.nextInt(100) == 1) { // wypadek raz na 100
                    stan = WYPADEK;
                    continue;
                }

                paliwo -= rand.nextInt(300);
                kilometrow_do_przejazdu -= rand.nextInt(10);
                if (kilometrow_do_przejazdu <= 0) {
                    stan = CZEKANIE_NA_KLIENTA;
                    if (dzisiejszych_klientow % 10 == 0) {
                        stan = PRZERWA;
                    }
                }
                if (paliwo <= REZERWA) {
                    paliwo += Math.max(kanister, 0); // jezeli jest paliwo w kanistrze, to zatankuj
                    kanister = 0;
                    stan = BRAK_PALIWA;
                } else try {
                    sleep(rand.nextInt(1000));
                } catch (Exception e) {
                }
            } else if (stan == PRZERWA) {
                if (dlugosc_zmiany <= 0) {
                    stan = KONIEC;
                    continue;
                }
                dlugosc_zmiany -= (30);
                paliwo += TANKUJ;
                paliwo = Math.min(paliwo, MAX_PALIWO);
                try {
                    sleep(rand.nextInt(1000));
                } catch (Exception e) {
                }
                stan = CZEKANIE_NA_KLIENTA;
            } else if (stan == CZEKANIE_NA_KLIENTA) {
                dlugosc_zmiany -= rand.nextInt(10);
                if (dlugosc_zmiany <= 0) {
                    stan = KONIEC;
                    continue;
                }
                System.out.println("#" + numer + " Taxi w oczekiwaniu na klienta w trakcie postoju.");
                if (rand.nextInt(2) == 1) {
                    dzisiejszych_klientow++;
                    System.out.println("#" + numer + " Taxi w trakcie pracy. Dzisiejszych klientów: " + dzisiejszych_klientow);
                    kilometrow_do_przejazdu = rand.nextInt(15);
                    laczna_ilosc_kilometrow += kilometrow_do_przejazdu;
                    stan = taxi.start(numer);
                } else {
                    System.out.println("#" + numer + " Taxi dalej czeka w trakcie postoju.");
                    try {
                        sleep(rand.nextInt(1000));
                    } catch (Exception e) {
                    }
                }
            } else if (stan == BRAK_PALIWA) {
                dlugosc_zmiany -= 30;
                System.out.println("#" + numer + " Kierowca nie potrafi znaleźć czasu na tankowanie.");
                dzisiejszych_klientow--;
                stan = BAZA;
            } else if (stan == WYPADEK) {
                System.out.println("#" + numer + " Wypadek. Taxi niezdolne do dalszego przejazdu");
                taxi.zmniejsz();
                stan = KONIEC;
            } else if (stan == KONIEC) {
                System.out.println("#" + numer + " Taxi zakonczylo prace.");
                System.out.println("Ilosc klientow: " + dzisiejszych_klientow);
                taxi.koniec(laczna_ilosc_kilometrow, dzisiejszych_klientow, dlugosc_zmiany);
                break;
            }
        }
    }
}