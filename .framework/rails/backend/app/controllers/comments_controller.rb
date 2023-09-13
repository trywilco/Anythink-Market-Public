# frozen_string_literal: true

class CommentsController < ApplicationController
  before_action :authenticate_user!, except: [:index]
  before_action :find_item!

  def index
    @comments = @item.comments.order(created_at: :desc)
  end

  def create
    @comment = @item.comments.new(comment_params)
    @comment.user = current_user

    render json: { errors: @comment.errors }, status: :unprocessable_entity unless @comment.save
  end

  def destroy
    @comment = @item.comments.find(params[:id])

    @comment.destroy
    render json: {}
  end

  private

  def comment_params
    params.require(:comment).permit(:body)
  end

  def find_item!
    @item = Item.find_by!(slug: params[:item_slug])
  end
end
