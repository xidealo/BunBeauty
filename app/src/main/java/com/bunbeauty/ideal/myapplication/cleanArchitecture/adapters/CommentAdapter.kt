package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

/*class CommentAdapter(private var numberItems: Int, private var commentList: ArrayList<Comment>?) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private var context: Context? = null
    fun refreshData(numberItems: Int, commentList: ArrayList<Comment>) {
        this.numberItems = numberItems
        this.commentList = commentList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CommentViewHolder {
        context = viewGroup.context
        val layoutIdForListItem = R.layout.comment_element
        //Класс, который позволяет создавать представления из xml файла
        val layoutInflater = LayoutInflater.from(context)
        // откуда, куда, необходимо ли помещать в родителя
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return CommentAdapter.CommentViewHolder(view)
    }

    override fun onBindViewHolder(commentViewHolder: CommentViewHolder, i: Int) {
        commentViewHolder.bind(commentList!![i])
    }

    override fun getItemCount(): Int {
        return numberItems
    }

    internal inner class CommentViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(comment: Comment) {
            val commentElement = CommentElement(comment, view, context)
            commentElement.createElement()
        }
    }

    companion object {
        private val TAG = "DBInf"
    }

}*/
