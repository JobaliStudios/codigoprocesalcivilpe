package com.jobalistudios.codigoprocesalcivilpe.busqueda;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jobalistudios.codigoprocesalcivilpe.navigation.ArticleNavigationResolver;

/** Resuelve un input corto a un artículo exacto, sin ejecutar una búsqueda de contenido. */
public final class GoToArticleResolver {

    @NonNull
    public Result resolve(@NonNull Context context, @Nullable String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return Result.error(Status.EMPTY, null);
        }

        String normalizedNumber = ArticleNumberQueryParser.extract(userInput);
        if (normalizedNumber == null) {
            return Result.error(Status.INVALID_FORMAT, null);
        }

        ArticleNavigationResolver.Target target = ArticleNavigationResolver.resolve(
                context,
                normalizedNumber
        );
        if (target == null) {
            return Result.error(Status.NOT_FOUND, normalizedNumber);
        }
        return Result.valid(target);
    }

    public enum Status {
        VALID,
        EMPTY,
        INVALID_FORMAT,
        NOT_FOUND
    }

    public static final class Result {
        @NonNull public final Status status;
        @Nullable public final String normalizedNumber;
        @Nullable public final ArticleNavigationResolver.Target target;

        private Result(
                @NonNull Status status,
                @Nullable String normalizedNumber,
                @Nullable ArticleNavigationResolver.Target target
        ) {
            this.status = status;
            this.normalizedNumber = normalizedNumber;
            this.target = target;
        }

        private static Result error(@NonNull Status status, @Nullable String normalizedNumber) {
            return new Result(status, normalizedNumber, null);
        }

        private static Result valid(@NonNull ArticleNavigationResolver.Target target) {
            return new Result(Status.VALID, target.getNumber(), target);
        }
    }
}
