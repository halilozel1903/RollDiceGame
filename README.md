# Roll Dice Game

<img src="dice.jpg" width="300" alt="Roll Dice Game" />

Roll Dice Game, klasik iki zar atma deneyimini etap hedefleri, XP/coin ekonomisi, kalıcı yükseltmeler, rozetler ve atış geçmişiyle daha oyunlaştırılmış bir Android deneyimine dönüştüren Kotlin + Jetpack Compose projesidir.

## Son Güncellemeler

- Proje arayüzü Jetpack Compose ve Material 3 ile yeniden kurgulandı.
- Oyun artık `Roll Dice Quest` akışı üzerinden etap etap ilerliyor.
- 6 farklı etap eklendi: Başlangıç Masası, Çift Zar Avı, Risk Koridoru, Kritik Seri, Şampiyon Masası ve Efsane Turu.
- Her etap için hedef skor, maksimum zar hakkı, XP ödülü ve coin ödülü tanımlandı.
- Zar atışlarına skor sistemi eklendi: zar toplamı, çift zar bonusu, kombo bonusu ve yükseltme bonusları birlikte hesaplanır.
- XP, oyuncu seviyesi ve seviye içi ilerleme barları eklendi.
- Coin sistemi eklendi ve coinler yükseltme satın almak için kullanılıyor.
- 3 kalıcı yükseltme eklendi:
  - Ek Deneme: her etapta 1 ekstra zar hakkı.
  - Çift Zar Bonus: çift zar geldiğinde ek skor.
  - Usta Antrenmanı: etap ödüllerinden ekstra XP.
- 7 rozet eklendi:
  - İlk Atış
  - Çifte Güç
  - On İki
  - Seri Ustası
  - Kasa Dolu
  - Beşinci Masa
  - Final Masası
- Rozetler XP ve coin ödülleriyle birlikte açılıyor.
- Alt navigasyon eklendi: Oyun, Güçler, Rozetler ve Geçmiş.
- Son atış geçmişi, en iyi kombo ve son zar bilgileri takip ediliyor.
- Oyun mesajları snackbar/effect akışı üzerinden gösteriliyor.
- Oyun mantığı UI'dan ayrıldı; reducer tabanlı, test edilebilir bir state yapısı eklendi.
- Reducer için unit testler eklendi.
- Uygulama sürümü `2.0` olarak güncellendi.

## Oynanış

Oyuncu iki zar atarak mevcut etabın hedef skoruna ulaşmaya çalışır. Her atış zarların toplamı kadar skor kazandırır. Çift zar geldiğinde ekstra bonus verilir; arka arkaya çift zarlar kombo skorunu büyütür.

Etap hedefi tamamlanırsa oyuncu XP ve coin kazanıp sonraki masaya geçer. Zar hakkı biterse etap yeniden başlar, ancak oyuncu teselli XP'si alır. Coinler kalıcı yükseltmeler için harcanır; rozetler ise başarılara göre otomatik açılır.

## Özellikler

- İki zarlı rastgele atış sistemi
- Etap bazlı hedef ve ilerleme sistemi
- XP, oyuncu seviyesi ve coin ekonomisi
- Kombo, çift zar ve yükseltme bonusları
- Kalıcı yükseltmeler
- Ödüllü rozet koleksiyonu
- Son atış geçmişi
- Sıfırlama aksiyonu
- Material 3 tabanlı modern Compose arayüzü
- Edge-to-edge ekran desteği

## Teknik Yapı

Proje Kotlin ile yazıldı ve Android UI katmanında Jetpack Compose kullanılıyor.

- `MainActivity.kt`: Compose ekranları, navigasyon, tema ve UI bileşenleri.
- `DiceGameViewModel.kt`: Kullanıcı intent'lerini alır, state'i günceller ve tek seferlik effect'leri yayar.
- `DiceGameContract.kt`: UI intent ve effect sözleşmeleri.
- `DiceGameReducer.kt`: Oyun kurallarını, skor hesaplamayı, etap geçişlerini, yükseltmeleri ve rozet açma mantığını yönetir.
- `DiceGameModels.kt`: Oyun state'i, etap, zar, yükseltme, rozet ve geçmiş modelleri.
- `DiceGameCatalog.kt`: Etap, yükseltme ve rozet katalogları.
- `DiceRoller.kt`: Zar atma davranışını soyutlayan arayüz ve rastgele zar üretici.

## Kullanılan Teknolojiler

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Lifecycle
- Kotlin Coroutines / Flow
- JUnit 4
- Android Gradle Plugin 9.2.1
- Kotlin Compose plugin 2.3.21
- Compile SDK 36
- Min SDK 24

## Kurulum

Projeyi Android Studio ile açıp çalıştırabilirsiniz.

Komut satırından debug build almak için:

```bash
./gradlew :app:assembleDebug
```

Unit testleri çalıştırmak için:

```bash
./gradlew test
```

## Ekran Görüntüleri

Depodaki mevcut ekran görüntüleri:

<img src="Screenshot_1539290025.png" width="200" alt="Screenshot 1" />
<img src="Screenshot_1539290034.png" width="200" alt="Screenshot 2" />
<img src="Screenshot_1539290044.png" width="200" alt="Screenshot 3" />

## Lisans

```text
MIT License

Copyright (c) 2023 Halil OZEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
