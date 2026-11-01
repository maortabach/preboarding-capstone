package com.example.tasks;

/**
 * Data Transfer Object representing the count of total tasks and completed tasks.
 * Edge cases:
 * - When no tasks exist, total = 0 and done = 0.
 * - When all tasks are completed, total == done.
 * - Negative counts are not possible as they represent non-negative collections.
 *
 * @param total the total number of tasks
 * @param done the number of completed tasks
 */
public record TaskCount(long total, long done) {
}
