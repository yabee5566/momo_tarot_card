package com.onean.momo.ui.draw_card.model

import androidx.annotation.DrawableRes
import com.onean.momo.R
import com.onean.momo.data.network.response.TarotCardDetail

data class DrawnTarotCardUiModel(
    @DrawableRes val cardDrawableId: Int,
    val isCardUpright: Boolean,
    val cardNameWithDirection: String,
    val answerFromCard: String
)

fun TarotCardDetail.toUiModel(): DrawnTarotCardUiModel {
    return DrawnTarotCardUiModel(
        cardDrawableId = tarotCardNumber.toTarotDrawableId(),
        isCardUpright = isTarotCardUpRight,
        cardNameWithDirection = cardNameWithDirection,
        answerFromCard = answerFromCard
    )
}

fun Int.toTarotDrawableId(): Int {
    val tarotCardNum = this
    return TAROT_CARD_DRAWABLE_ID_ARRAY.getOrElse(tarotCardNum) {
        throw IllegalArgumentException("tarotCardNum: $tarotCardNum is out of range")
    }
}

// don't know why if we defined this in arrays.xml, some of drawables res id would be 0.
// So we define it here.
val TAROT_CARD_DRAWABLE_ID_ARRAY = arrayOf(
    R.drawable.fool_0,
    R.drawable.magician_1,
    R.drawable.high_priestess_2,
    R.drawable.empress_3,
    R.drawable.emperor_4,
    R.drawable.hierophant_5,
    R.drawable.lovers_6,
    R.drawable.chariot_7,
    R.drawable.strength_8,
    R.drawable.hermit_9,
    R.drawable.wheel_of_fortune_10,
    R.drawable.justice_11,
    R.drawable.hanged_man_12,
    R.drawable.death_13,
    R.drawable.temperance_14,
    R.drawable.devil_15,
    R.drawable.tower_16,
    R.drawable.star_17,
    R.drawable.moon_18,
    R.drawable.sun_19,
    R.drawable.judgement_20,
    R.drawable.world_21,
    R.drawable.wands01_22,
    R.drawable.wands02_23,
    R.drawable.wands03_24,
    R.drawable.wands04_25,
    R.drawable.wands05_26,
    R.drawable.wands06_27,
    R.drawable.wands07_28,
    R.drawable.wands08_29,
    R.drawable.wands09_30,
    R.drawable.wands10_31,
    R.drawable.wands11_32,
    R.drawable.wands12_33,
    R.drawable.wands13_34,
    R.drawable.wands14_35,
    R.drawable.cups01_36,
    R.drawable.cups02_37,
    R.drawable.cups03_38,
    R.drawable.cups04_39,
    R.drawable.cups05_40,
    R.drawable.cups06_41,
    R.drawable.cups07_42,
    R.drawable.cups08_43,
    R.drawable.cups09_44,
    R.drawable.cups10_45,
    R.drawable.cups11_46,
    R.drawable.cups12_47,
    R.drawable.cups13_48,
    R.drawable.cups14_49,
    R.drawable.swords01_50,
    R.drawable.swords02_51,
    R.drawable.swords03_52,
    R.drawable.swords04_53,
    R.drawable.swords05_54,
    R.drawable.swords06_55,
    R.drawable.swords07_56,
    R.drawable.swords08_57,
    R.drawable.swords09_58,
    R.drawable.swords10_59,
    R.drawable.swords11_60,
    R.drawable.swords12_61,
    R.drawable.swords13_62,
    R.drawable.swords14_63,
    R.drawable.pents01_64,
    R.drawable.pents02_65,
    R.drawable.pents03_66,
    R.drawable.pents04_67,
    R.drawable.pents05_68,
    R.drawable.pents06_69,
    R.drawable.pents07_70,
    R.drawable.pents08_71,
    R.drawable.pents09_72,
    R.drawable.pents10_73,
    R.drawable.pents11_74,
    R.drawable.pents12_75,
    R.drawable.pents13_76,
    R.drawable.pents14_77
)
