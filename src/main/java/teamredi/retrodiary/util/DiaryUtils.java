package teamredi.retrodiary.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DiaryUtils {

    public static String integerMoodToString(Integer integerMood) {
        String stringMood = "";
        switch (integerMood) {
            case 1:
                stringMood = "나도 내기분을 모르겠어";
                break;
            case 2:
                stringMood = "좋아";
                break;
            case 3:
                stringMood = "그저그래";
                break;
            case 4:
                stringMood = "별로야";
                break;
            case 5:
                stringMood = "상큼해";
                break;
            case 6:
                stringMood = "멋쟁이야";
                break;
            case 7:
                stringMood = "좋지않아";
                break;
            default:
                stringMood = "정말 화가나";
                break;
        }
        return stringMood;
    }



    public static String integerWeatherToString(Integer integerWeather){
        String stringWeather = "";
        switch (integerWeather) {
            case 1:
                stringWeather = "완벽함";
                break;
            case 2:
                stringWeather = "맑음";
                break;
            case 3:
                stringWeather = "약간 흐림";
                break;
            case 4:
                stringWeather = "달이 이쁨";
                break;
            case 5:
                stringWeather = "밤하늘이 이쁨";
                break;
            case 6:
                stringWeather = "흐림";
                break;
            case 7:
                stringWeather = "비";
                break;
            default:
                stringWeather = "천둥 번개";
                break;
        }
        return stringWeather;

    }

    public static LocalDate stringToLocalDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        log.info(LocalDate.parse(stringDate, formatter).toString());
        return LocalDate.parse(stringDate, formatter);
    }
}


