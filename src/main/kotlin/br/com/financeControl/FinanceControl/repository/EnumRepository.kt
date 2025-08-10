package br.com.financeControl.FinanceControl.repository

enum class EnumRepository (val parament : String) {
    ATTEMPTS("attempts"),
    EMAIL("email"),
    LOCKED("locked"),
    LOCKEDTIME("lockTime");

    companion object {
        fun fromRespository(parament: String): EnumRepository? {
            return EnumRepository.entries.find { it.parament.equals(parament, ignoreCase = true) }
        }
    }
}